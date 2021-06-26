package com.example.rsocketserver.data;

import java.time.Instant;

import lombok.Data;
import lombok.ToString;

@Data //incluye todos
public class Message {
    private String origin;
    private String interaction;
    private long index;
    private long created = Instant.now().getEpochSecond();

    public Message() {}

	public Message(String origin, String interaction) {
        this.origin = origin;
        this.interaction = interaction;
        this.index = 0;
    }

    public Message(String origin, String interaction, long index) {
        this.origin = origin;
        this.interaction = interaction;
        this.index = index;
    }

	public String getOrigin() {
		return origin;
	}

	public String getInteraction() {
		return interaction;
	}

	public long getIndex() {
		return index;
	}

	public long getCreated() {
		return created;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public void setInteraction(String interaction) {
		this.interaction = interaction;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "Message [origin=" + origin + ", interaction=" + interaction + ", index=" + index + ", created="
				+ created + "]";
	}
	
}
