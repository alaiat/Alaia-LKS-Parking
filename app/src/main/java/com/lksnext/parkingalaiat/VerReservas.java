package com.lksnext.parkingalaiat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VerReservas extends AppCompatActivity {

    private Button add;
    private EditText etReserva;
    private RecyclerView rvReserva;

    private ReservaAdapter adapter;



    List<String> reservas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_reservas);
        initHelpers();
        initUi();
    }

    private void initUi() {
       initView();
       initListeners();
       initRecyclerView();
    }

    private void initRecyclerView() {
        rvReserva.setLayoutManager(new LinearLayoutManager(this));
        adapter=new ReservaAdapter(reservas);
        rvReserva.setAdapter(adapter);

    }

    private void initHelpers(){

    }

    private void initView() {
        add=findViewById(R.id.button);
        etReserva=findViewById(R.id.textView2);
        rvReserva=findViewById(R.id.rvReserva);
    }

    private void initListeners(){
        add.setOnClickListener(v ->{addReserva();});
    }

    private void addReserva() {
        String reservaParaAñadir=etReserva.getText().toString();
        reservas.add(reservaParaAñadir);
        adapter.notifyDataSetChanged();
        etReserva.setText("");
    }

}
