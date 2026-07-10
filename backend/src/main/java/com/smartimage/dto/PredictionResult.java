package com.smartimage.dto;

import java.util.List;

public class PredictionResult {
    private String filename;
    private int topK;
    private List<PredictionItem> predictions;

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }
    public List<PredictionItem> getPredictions() { return predictions; }
    public void setPredictions(List<PredictionItem> predictions) { this.predictions = predictions; }

    public static class PredictionItem {
        private int rank;
        private int classId;
        private String label;
        private double confidence;

        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
        public int getClassId() { return classId; }
        public void setClassId(int classId) { this.classId = classId; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }
}
