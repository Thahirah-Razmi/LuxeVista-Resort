package com.example.luxevistaresort;public class Room {
        private int roomId;
        private String roomType;
        private String description;
        private int pricePerNight;
        private String availability;

    public Room(int roomId, String roomType, String description, int pricePerNight, String availability) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.availability = availability;
    }

        public int getRoomId() { return roomId; }
        public String getRoomType() { return roomType; }
        public String getDescription() { return description; }
        public int getPricePerNight() { return pricePerNight; }
        public String getAvailability() { return availability; }
}
