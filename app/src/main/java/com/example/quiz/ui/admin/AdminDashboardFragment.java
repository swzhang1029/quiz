package com.example.quiz.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.quiz.R;
import com.google.android.material.button.MaterialButton;

public class AdminDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton createTournamentButton = view.findViewById(R.id.createTournamentButton);
        MaterialButton viewTournamentsButton = view.findViewById(R.id.viewTournamentsButton);

        createTournamentButton.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_admin_to_create_tournament));

        viewTournamentsButton.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_admin_to_tournament_list));
    }
} 