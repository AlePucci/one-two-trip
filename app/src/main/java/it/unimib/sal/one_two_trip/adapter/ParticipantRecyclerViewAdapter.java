package it.unimib.sal.one_two_trip.adapter;

import android.util.Log;
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

    public ParticipantRecyclerViewAdapter(List<Person> personList) {
        this.personList = personList;
    }

    public class ParticipantHolder extends RecyclerView.ViewHolder {
        private final TextView item_name;

        public ParticipantHolder(@NonNull View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.participant_name);
        }

        public void bind(Person person) {
            item_name.setText(person.getName() + " " + person.getSurname());
        }
    }

    @NonNull
    @Override
    public ParticipantHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_item, parent, false);

        return new ParticipantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantHolder holder, int position) {
        Person person = personList.get(position);
        holder.bind(person);
    }

    @Override
    public int getItemCount() {
        if(personList == null) {
            return 0;
        }

        return personList.size();
    }
}
