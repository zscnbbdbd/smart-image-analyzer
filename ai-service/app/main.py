"""
AI Inference Microservice - Image Classification with PyTorch
"""
import io, json, torch
import torchvision.transforms as transforms
import torchvision.models as models
from pathlib import Path
from fastapi import FastAPI, File, UploadFile, HTTPException
from PIL import Image

app = FastAPI(title="AI Image Classifier", version="1.0.0")

IMAGENET_CLASSES = []
CLASSES_PATH = Path(__file__).parent / "imagenet_classes.txt"
if CLASSES_PATH.exists():
    with open(CLASSES_PATH, "r", encoding="utf-8") as f:
        IMAGENET_CLASSES = [line.strip() for line in f.readlines()]

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = models.resnet18(weights=models.ResNet18_Weights.IMAGENET1K_V1)
model.eval()
model.to(device)

transform = transforms.Compose([
    transforms.Resize(256),
    transforms.CenterCrop(224),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])

@app.get("/health")
def health_check():
    return {"status": "ok", "device": str(device)}

@app.post("/predict")
async def predict(file: UploadFile = File(...), top_k: int = 5):
    if not file.content_type or not file.content_type.startswith("image/"):
        raise HTTPException(400, "Please upload a valid image file")
    contents = await file.read()
    try:
        image = Image.open(io.BytesIO(contents)).convert("RGB")
    except Exception:
        raise HTTPException(400, "Failed to parse image file")
    input_tensor = transform(image).unsqueeze(0).to(device)
    with torch.no_grad():
        output = model(input_tensor)
    probabilities = torch.nn.functional.softmax(output[0], dim=0)
    top_probs, top_indices = torch.topk(probabilities, top_k)
    results = []
    for i in range(top_k):
        idx = top_indices[i].item()
        label = IMAGENET_CLASSES[idx] if idx < len(IMAGENET_CLASSES) else f"class_{idx}"
        results.append({
            "rank": i + 1,
            "class_id": idx,
            "label": label,
            "confidence": round(top_probs[i].item(), 4),
        })
    return {
        "filename": file.filename,
        "top_k": top_k,
        "predictions": results,
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)