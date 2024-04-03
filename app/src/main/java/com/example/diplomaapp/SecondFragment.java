package com.example.diplomaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.diplomaapp.dataClasses.DBHelper;
import com.example.diplomaapp.dataClasses.System;
import com.example.diplomaapp.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    private DBHelper dbHelper;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonSave.setOnClickListener(v -> {
            Boolean nameEmpty = binding.textEditSystemName.getText().toString().isEmpty();
            if(!nameEmpty) {
                String sysName = binding.textEditSystemName.getText().toString();
                Bundle args = getArguments();
                String address = "";
                String login = "";
                String password = "";

                if (args != null) {
                    address = args.getString("address");
                    if (args.containsKey("login")) {
                        login = args.getString("login");
                    }
                    if (args.containsKey("password")) {
                        login = args.getString("password");
                    }

                    dbHelper = new DBHelper(requireContext());
                    System mySys = new System(sysName, address);

                    if(!login.isEmpty() && !password.isEmpty())
                    {
                        mySys.setMqtt_login(login);
                        mySys.setMqtt_password(password);
                    }

                    dbHelper.addSystem(mySys);
                    Toast.makeText(requireContext(), "Successfully added system!", Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(SecondFragment.this)
                            .navigate(R.id.action_SecondFragment_to_mainActivity);
                }
            }else Toast.makeText(requireContext(), "System name field should be filled", Toast.LENGTH_LONG).show();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}