package com.example.diplomaapp;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.diplomaapp.dataClasses.DBHelper;
import com.example.diplomaapp.dataClasses.PortFilter;
import com.example.diplomaapp.dataClasses.Storage;
import com.example.diplomaapp.dataClasses.System;
import com.example.diplomaapp.databinding.FragmentFirstBinding;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;

    private static System system;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

//        InputFilter[] filters = new InputFilter[1];
//        filters[0] = new IPAddressFilter();
//        binding.editTextInputIP.setFilters(filters);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new PortFilter();
        binding.editTextInputPort.setFilters(filters);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (Storage.system != null) {
                try {
                    URI uri = new URI(Storage.system.getMqtt_url());
                    binding.editTextInputIP.setText(uri.getHost());
                    binding.editTextInputPort.setText(String.valueOf(uri.getPort()));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                binding.editTextInputLogin.setText(Storage.system.getMqtt_login());
                binding.editTextPassword.setText(Storage.system.getMqtt_password());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        binding.buttonFirst.setOnClickListener(v ->{
                Bundle bndl = validateData();
                if(bndl != null)
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment, bndl);
        }
        );
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_mainActivity)
        );
    }

    public Bundle validateData(){
        Bundle bundle = new Bundle();

        if(binding.editTextInputIP.getText().toString().isEmpty() || Objects.requireNonNull(binding.editTextInputPort.getText()).toString().isEmpty()) {
            Toast.makeText(requireContext(), "Required IP Address and port", Toast.LENGTH_LONG).show();
            return null;
        }
        else {
            String ipAddress = binding.editTextInputIP.getText().toString();
            String port = binding.editTextInputPort.getText().toString();
            if (isValidPort(port)) {
                Boolean loginEmpty = binding.editTextInputLogin.getText() == null;
                Boolean passwordEmpty = binding.editTextPassword.getText() == null;
                if (loginEmpty && passwordEmpty) {
                    bundle.putString("address", "tcp://" + ipAddress + ":" + port);
                    return bundle;
                } else if (!loginEmpty && !passwordEmpty) {
                    bundle.putString("address", "tcp://" + ipAddress + ":" + port);
                    bundle.putString("login", binding.editTextInputLogin.getText().toString());
                    bundle.putString("password", binding.editTextPassword.getText().toString());
                    return bundle;
                } else {
                    Toast.makeText(requireContext(), "Required full credentials or none", Toast.LENGTH_LONG).show();
                    return null;
                }
            }
            else{
                Toast.makeText(requireContext(), "Incorrect port", Toast.LENGTH_LONG).show();
                return null;
            }
        }
    }

    private boolean isValidIPAddress(String ipAddress) {
        // Регулярное выражение для проверки IP-адреса
        String ipRegex = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ipAddress.matches(ipRegex);
    }

    private boolean isValidPort(String port) {
        // Регулярное выражение для проверки порта (число от 1 до 65535)
        String portRegex = "^([1-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-5]{2}[0-3][0-5])$";
        return port.matches(portRegex);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}