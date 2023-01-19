package it.unimib.sal.one_two_trip.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Person;

public class ParticipantRecyclerViewAdapter
        extends RecyclerView.Adapter<ParticipantRecyclerViewAdapter.ParticipantHolder> {

    private final List<Person> personList;
    private final OnItemClickListener onItemClickListener;

    public ParticipantRecyclerViewAdapter(List<Person> personList, OnItemClickListener onItemClickListener) {
        this.personList = personList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ParticipantHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new ParticipantHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantHolder holder, int position) {
        holder.bind(this.personList.get(position));
    }

    @Override
    public int getItemCount() {
        if (this.personList == null) {
            return 0;
        }

        return this.personList.size();
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public class ParticipantHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView itemName;

        public ParticipantHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.participant_name);
        }

        public void bind(Person person) {
            itemName.setText(person.getName() + " " + person.getSurname());
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onClick(getAdapterPosition());
        }
    }
}
