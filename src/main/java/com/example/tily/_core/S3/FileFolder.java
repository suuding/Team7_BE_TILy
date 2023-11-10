package com.example.tily._core.S3;

public enum FileFolder {
    USER_IMAGE {
        @Override
        public String getFolder(S3Component component) {
            return component.getUserFolder() + "/";
        }
    },
    ROADMAP_IMAGE {
        @Override
        public String getFolder(S3Component component) {
            return component.getRoadmapFolder() + "/";
        }
    },
    POST_IMAGE {
        @Override
        public String getFolder(S3Component component) {
            return component.getPostFolder() + "/";
        }
    };

    public abstract String getFolder(S3Component component);
}
