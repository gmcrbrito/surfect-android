package amsi.dei.estg.ipleiria.surfectstore.ui.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import amsi.dei.estg.ipleiria.surfectstore.Endpoints;
import amsi.dei.estg.ipleiria.surfectstore.R;
import amsi.dei.estg.ipleiria.surfectstore.activities.Register;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    // Enunciar os objetos
    private TextView primeiroNome, email, minusculas, maiusculas, numeros, caracteres;
    private EditText novaPassword, confirmarPassword, numeroTelemovel, dataDeNascimento, peso, altura;
    private Button atualizar, escolher;

    public ProfileFragment(){
        // construtor
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        inicializeComponents(v);

        // Obter o email guardado na SharedPreference
        SharedPreferences prefs = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String _email = prefs.getString("email", "Nenhum email definido.");

        // Obter a string a atividade anterior
        String extraEmail = getActivity().getIntent().getStringExtra("email");

        // Atribuir os valores das strings às EditText
        email.setText(extraEmail);

        // Clicar no botão registar
        atualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Atualizar();
            }
        });

        escolher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Funcionalidade da foto de perfil ainda não está disponível.", Toast.LENGTH_SHORT).show();
            }
        });

        // Ao clicar na data de nascimento, aparece um datepicker dialog para escolher a data
        Calendar calendario = Calendar.getInstance();
        final int ano = calendario.get(Calendar.YEAR);
        final int mes = calendario.get(Calendar.MONTH);
        final int dia = calendario.get(Calendar.DAY_OF_MONTH);
        dataDeNascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                        mes = mes + 1;
                        String data = ano + "/" + mes + "/" + dia;
                        dataDeNascimento.setText(data);
                    }
                }, ano, mes, dia);
                datePickerDialog.show();
            }
        });

        // Obter todos os caracteres da password
        novaPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String Password = novaPassword.getText().toString();
                ValidarPassword(Password);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }

    // Procedimento para atualizar o perfil do utilizador
    private void Atualizar(){

        final String _email = this.email.getText().toString().trim();
        final String _novaPassword = this.novaPassword.getText().toString().trim();
        final String _confirmarPassword = this.confirmarPassword.getText().toString().trim();
        final String _dataDeNascimento = this.dataDeNascimento.getText().toString().trim();
        final String _numeroTelemovel = this.numeroTelemovel.getText().toString().trim();
        final String _peso = this.peso.getText().toString().trim();
        final String _altura = this.altura.getText().toString().trim();

        if (_numeroTelemovel.equals("")){
            Toast.makeText(getActivity(), "É obrigatório inserir o seu número de telemóvel!", Toast.LENGTH_SHORT).show();
        }
        else if (_dataDeNascimento.equals("")){
            Toast.makeText(getActivity(), "É obrigatório inserir a sua data de nascimento!", Toast.LENGTH_SHORT).show();
        }
        else if (_numeroTelemovel.length() < 9){
            Toast.makeText(getActivity(), "O número de telemóvel tem de ter os caractéres obrigatórios!", Toast.LENGTH_SHORT).show();
        }
        else if (!_novaPassword.equals(_confirmarPassword)){
            Toast.makeText(getActivity(), "A nova password não coincide com a confirmação!", Toast.LENGTH_SHORT).show();
        }
        else if (maiusculas.getCurrentTextColor() == Color.RED || minusculas.getCurrentTextColor() == Color.RED || numeros.getCurrentTextColor() == Color.RED || caracteres.getCurrentTextColor() == Color.RED){
            Toast.makeText(getActivity(), "É obrigatório cumprir os requisitos da password!", Toast.LENGTH_SHORT).show();
        }
        else {

            String epURL_UPDATE = Endpoints.URL_UPDATE;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, epURL_UPDATE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equals("Perfil do utilizador editado com sucesso!")){
                                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), "Não registado! Tente outra vez! " + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", _email);
                    params.put("password", _novaPassword);
                    params.put("dataDeNascimento", _dataDeNascimento);
                    params.put("numeroTelemovel", _numeroTelemovel);
                    params.put("peso", _peso);
                    params.put("altura", _altura);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

    // Procedimento para validar se a password contém os requisitos necessários
    public void ValidarPassword(String novaPassword) {
        Pattern upperCase = Pattern.compile("[A-Z]");
        Pattern lowerCase = Pattern.compile("[a-z]");
        Pattern digitCase = Pattern.compile("[0-9]");

        if (!lowerCase.matcher(novaPassword).find()) {
            minusculas.setTextColor(Color.RED);
        } else {
            minusculas.setTextColor(Color.GREEN);
        }

        if (!upperCase.matcher(novaPassword).find()) {
            maiusculas.setTextColor(Color.RED);
        } else {
            maiusculas.setTextColor(Color.GREEN);
        }

        if (!digitCase.matcher(novaPassword).find()) {
            numeros.setTextColor(Color.RED);
        } else {
            numeros.setTextColor(Color.GREEN);
        }

        if (novaPassword.length() < 8) {
            caracteres.setTextColor(Color.RED);
        } else {
            caracteres.setTextColor(Color.GREEN);
        }
    }

    public void inicializeComponents(View v){
        // Achar os objetos no xml
        // TextView
        primeiroNome = (TextView)v.findViewById(R.id.per_tvPrimeiroNome);
        email = (TextView)v.findViewById(R.id.per_tvEmail);
        minusculas = (TextView)v.findViewById(R.id.per_tvMinusculas);
        maiusculas = (TextView)v.findViewById(R.id.per_tvMaiusculas);
        numeros = (TextView)v.findViewById(R.id.per_tvNumeros);
        caracteres = (TextView)v.findViewById(R.id.per_tvCaracteres);
        // EditText
        numeroTelemovel = (EditText) v.findViewById(R.id.per_etTelemovel);
        dataDeNascimento = (EditText) v.findViewById(R.id.per_etDataDeNascimento);
        novaPassword = (EditText) v.findViewById(R.id.per_etNovaPassword);
        confirmarPassword = (EditText) v.findViewById(R.id.per_etConfirmarPassword);
        peso = (EditText)v.findViewById(R.id.per_etPeso);
        altura = (EditText)v.findViewById(R.id.per_etAltura);
        // Button
        atualizar = (Button) v.findViewById(R.id.per_btAtualizar);
        escolher = (Button) v.findViewById(R.id.per_btEscolher);

    }
}