package com.example.myfirebaseapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myfirebaseapp.model.Pessoa;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private List<Pessoa> listPerson = new ArrayList<Pessoa>();
    ArrayAdapter<Pessoa> arrayAdapterPessoa;

    EditText nomP, appP, emailP, senhaP;
    ListView listV_pessoas;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Pessoa pessoaSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nomP = findViewById(R.id.txt_nomePessoa);
        appP = findViewById(R.id.txt_appPessoa);
        emailP = findViewById(R.id.txt_emailPessoa);
        senhaP = findViewById(R.id.txt_passwordPessoa);

        listV_pessoas = findViewById(R.id.lv_dadosPessoas);
        inicializarFirebase();

        listarDados();


        listV_pessoas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelected = (Pessoa) parent.getItemAtPosition(position);
                nomP.setText(pessoaSelected.getNome());
                appP.setText(pessoaSelected.getSobrenome());
                emailP.setText(pessoaSelected.getEmail());
                senhaP.setText(pessoaSelected.getSenha());

            }
        });

    }

    private void listarDados() {
        databaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear();
                for (DataSnapshot objSnapShot : dataSnapshot.getChildren()){
                    Pessoa p = objSnapShot.getValue(Pessoa.class);
                    listPerson.add(p);

                    arrayAdapterPessoa = new ArrayAdapter<Pessoa>(MainActivity.this, android.R.layout.simple_list_item_1, listPerson);
                    listV_pessoas.setAdapter(arrayAdapterPessoa);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nome = nomP.getText().toString();
        String email = emailP.getText().toString();
        String senha = senhaP.getText().toString();
        String app = appP.getText().toString();

        switch (item.getItemId()) {
            case R.id.icon_add: {
                if (nome.equals("") || email.equals("") || senha.equals("") || app.equals("")){
                    valida();
                    break;
                }else {
                    Pessoa p = new Pessoa();
                    p.setUid(UUID.randomUUID().toString());
                    p.setNome(nome);
                    p.setSobrenome(app);
                    p.setEmail(email);
                    p.setSenha(senha);
                    databaseReference.child("Pessoa").child(p.getUid()).setValue(p);
                    Toast.makeText(this, "Adicionado", Toast.LENGTH_LONG).show();
                    limparCaixas();
                    }
                 break;
            }
            case R.id.icon_save: {
                Pessoa p = new Pessoa();
                p.setUid(pessoaSelected.getUid());
                p.setNome(nomP.getText().toString().trim());
                p.setSobrenome(appP.getText().toString().trim());
                p.setEmail(emailP.getText().toString().trim());
                p.setSenha(senhaP.getText().toString().trim());
                databaseReference.child("Pessoa").child(p.getUid()).setValue(p);
                Toast.makeText(this, "Atualizado", Toast.LENGTH_LONG).show();
                limparCaixas();
                break;
            }
            case R.id.icon_delete: {
                Pessoa p = new Pessoa();
                p.setUid(pessoaSelected.getUid());
                databaseReference.child("Pessoa").child(p.getUid()).removeValue();
                Toast.makeText(this, "Deletado", Toast.LENGTH_LONG).show();
                limparCaixas();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limparCaixas() {
        nomP.setText("");
        emailP.setText("");
        senhaP.setText("");
        appP.setText("");
    }

    private void valida(){
        String nome = nomP.getText().toString();
        String email = emailP.getText().toString();
        String senha = senhaP.getText().toString();
        String app = appP.getText().toString();

        if (nome.equals("")) {
            nomP.setError("Required");
        }
        else if (app.equals("")){
            appP.setError("Required");
        }
        else if (email.equals("")){
            emailP.setError("Required");
        }else if (senha.equals("")){
            senhaP.setError("Required");
        }
    }
}
