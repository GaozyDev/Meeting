package io.agora.meeting.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.agora.meeting.ui.R;
import io.agora.meeting.ui.http.body.resp.RoomListResp;

public class RoomRecyclerAdapter extends RecyclerView.Adapter<RoomRecyclerAdapter.RoomViewHolder> {

    private List<RoomListResp> roomList = new ArrayList<>();

    private OnRoomClickListener onRoomClickListener;

    public void setRoomList(List<RoomListResp> roomList) {
        this.roomList = roomList;
        notifyDataSetChanged();
    }

    public void setOnRoomClickListener(OnRoomClickListener onRoomClickListener) {
        this.onRoomClickListener = onRoomClickListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(R.layout.layout_meeting_room, parent, false);
        return new RoomViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.bind(roomList.get(position));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {

        private final TextView roomIndexTv;
        private final TextView usersTv;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomIndexTv = itemView.findViewById(R.id.meeting_room_index_tv);
            usersTv = itemView.findViewById(R.id.meeting_room_users_tv);
        }

        void bind(RoomListResp room) {
            roomIndexTv.setText(String.valueOf(room.index));
            usersTv.setText(String.valueOf(room.users));
            itemView.setOnClickListener(v -> onRoomClickListener.roomClick(room));
        }
    }

    public interface OnRoomClickListener {

        void roomClick(RoomListResp room);
    }
}