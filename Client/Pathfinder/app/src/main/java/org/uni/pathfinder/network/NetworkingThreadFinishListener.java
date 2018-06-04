package org.uni.pathfinder.network;

public interface NetworkingThreadFinishListener {
    void onNetworkingResult(int request_id, String message, Object data, boolean failure);
}
