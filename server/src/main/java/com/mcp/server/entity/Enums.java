package com.mcp.server.entity;

public final class Enums {

    private Enums() {
    }

    public enum LoyaltyTier {
        STANDARD, SILVER, GOLD, PLATINUM
    }

    public enum OrderStatus {
        PENDING, PAID, SHIPPED, DELIVERED, CANCELLED, RETURNED
    }

    public enum PaymentStatus {
        AUTHORIZED, CAPTURED, FAILED, REFUNDED, PARTIALLY_REFUNDED
    }

    public enum RefundType {
        GOODWILL, DUPLICATE_CHARGE, WARRANTY, RETURN, OTHER
    }

    public enum RefundStatus {
        REQUESTED, APPROVED, PROCESSED, REJECTED
    }

    public enum Channel {
        EMAIL, CHAT, PHONE
    }

    public enum Intent {
        REFUND_REQUEST, PRESALES_QUESTION, BILLING_ISSUE,
        WARRANTY_CLAIM, COMPLAINT, GENERAL, OTHER
    }

    public enum Sentiment {
        POSITIVE, NEUTRAL, NEGATIVE, ANGRY
    }

    public enum TicketStatus {
        OPEN, RESOLVED, ESCALATED
    }
}
