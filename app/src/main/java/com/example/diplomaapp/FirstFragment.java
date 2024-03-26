package com.example.diplomaapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.diplomaapp.databinding.FragmentFirstBinding;

import java.util.Objects;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonFirst.setOnClickListener(v ->{
                if(validateData())
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
        }
        );
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_mainActivity)
        );

    }

    public boolean validateData(){
        Bundle bundle = new Bundle();

        if(binding.editTextInputIP.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Required IP Address", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            Boolean loginEmpty = binding.editTextInputLogin.getText().toString().isEmpty();
            Boolean passwordEmpty = binding.editTextPassword.getText().toString().isEmpty();
            if(loginEmpty && passwordEmpty) {
                bundle.putString("address", binding.editTextInputIP.getText().toString());
                return true;
            }
            else if(!loginEmpty && !passwordEmpty){
                bundle.putString("address", binding.editTextInputIP.getText().toString());
                bundle.putString("login", Objects.requireNonNull(binding.editTextInputLogin.getText()).toString());
                bundle.putString("password", binding.editTextPassword.getText().toString());
                return true;
            } else {
                Toast.makeText(requireContext(), "Required full credentials or none", Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}