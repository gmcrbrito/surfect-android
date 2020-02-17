package amsi.dei.estg.ipleiria.surfectstore.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.zolad.zoominimageview.ZoomInImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import amsi.dei.estg.ipleiria.surfectstore.Endpoints;
import amsi.dei.estg.ipleiria.surfectstore.R;
import amsi.dei.estg.ipleiria.surfectstore.models.Morada;

public class ProductDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView nomeProduto, categoriaProduto, stockProduto, precoProduto, descricaoProduto;
    private ImageView fotoProduto;
    private Button efetuarCompra;
    int contador = 1;
    Dialog dialogCarrinho;
    private static final int STORAGE_CODE = 1000;
    private ArrayList<Morada> moradaArrayList;
    private ArrayList<String> names = new ArrayList<String>();
    private Spinner spinner;
    public String item, nomeMorada;
    String  tamanhoSelecionado;
    int idMorada,_precoProduto, _idProduto;
    List<Integer> idsMoradas = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Pôr logotipo como imagem na ActionBar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        _idProduto = getIntent().getExtras().getInt("idProduto");
        final String _nomeProduto = getIntent().getExtras().getString("nomeProduto");
        _precoProduto = getIntent().getExtras().getInt("precoProduto");
        int _stockProduto = getIntent().getExtras().getInt("stockProduto");
        String _descricaoProduto = getIntent().getExtras().getString("descricaoProduto");
        int _categoriaProduto = getIntent().getExtras().getInt("categoriaProduto");
        final String _fotoProduto = getIntent().getExtras().getString("fotoProduto");

        inicializeComponents();

        nomeProduto.setText(_nomeProduto);
        categoriaProduto.setText("Categoria: " + _categoriaProduto);
        stockProduto.setText("Em stock: " + _stockProduto);
        precoProduto.setText("" + _precoProduto);
        descricaoProduto.setText(_descricaoProduto);
        Glide.with(this).load(_fotoProduto).into(fotoProduto);

        dialogCarrinho = new Dialog(this);
        efetuarCompra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {dialogCarrinho.setContentView(R.layout.dialog_add_cart);
                TextView carrinhoNome = (TextView) dialogCarrinho.findViewById(R.id.car_tvNomeProduto);
                TextView carrinhoPreco = (TextView)dialogCarrinho.findViewById(R.id.car_tvPrecoProduto);
                final TextView contadorQuantidade = (TextView)dialogCarrinho.findViewById(R.id.car_tvContador);
                ImageView adicionarQuantidade = (ImageView)dialogCarrinho.findViewById(R.id.car_imgAdd);
                final ImageView retirarQuantidade = (ImageView)dialogCarrinho.findViewById(R.id.car_imgRemove);
                ZoomInImageView carrinhoFoto = (ZoomInImageView)dialogCarrinho.findViewById(R.id.car_imgProduto);
                Spinner spinnerTamanhos = (Spinner)dialogCarrinho.findViewById(R.id.car_spinnerTamanho);
                final Spinner spinnerMorada = (Spinner)dialogCarrinho.findViewById(R.id.car_spinnerMorada);
                Button comprar = (Button)dialogCarrinho.findViewById(R.id.car_btAdicionar);
                retrieveJSON(spinnerMorada);
                Glide.with(ProductDetails.this).load(_fotoProduto).into(carrinhoFoto);
                carrinhoNome.setText(_nomeProduto);
                carrinhoPreco.setText("Preço p/ item: " + _precoProduto + "€");



                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ProductDetails.this, R.array.tamanhos, R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTamanhos.setAdapter(adapter);
                spinnerTamanhos.setOnItemSelectedListener(ProductDetails.this);

                adicionarQuantidade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contador++;
                        contadorQuantidade.setText(Integer.toString(contador));
                    }
                });

                retirarQuantidade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(contador >= 2){
                            contador--;
                        }
                        contadorQuantidade.setText(Integer.toString(contador));
                    }
                });

                comprar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetails.this);
                        builder.setTitle("Comprar produto")
                                .setMessage("Tem a certeza que pretende comprar este produto?")
                                .setCancelable(true)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                                            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                                                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                                requestPermissions(permissions, STORAGE_CODE);
                                            }
                                            else {
                                                efetuarCompra();
                                                //savePdf();
                                            }
                                        } else {
                                            efetuarCompra();
                                            //savePdf();
                                        }
                                    }
                                });
                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
                spinnerMorada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        idMorada = idsMoradas.get(position);
                        nomeMorada = spinnerMorada.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
                dialogCarrinho.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogCarrinho.show();
            }
        });
    }

    private void retrieveJSON(final Spinner spinnerMorada) {

        SharedPreferences prefs = getSharedPreferences("userInfo", MODE_PRIVATE);
        final String extraEmail = prefs.getString("email", "Nenhum email definido.");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.URL_MORADAS,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if(response != null){

                            moradaArrayList = new ArrayList<>();
                            JSONArray dataArray  = obj.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {

                                Morada moradaModel = new Morada();
                                JSONObject dataobj = dataArray.getJSONObject(i);

                                moradaModel.setMoradaId(dataobj.getInt("address_id"));
                                moradaModel.setCodigoPostalMorada(dataobj.getString("zip_code"));
                                moradaModel.setDistritoMorada(dataobj.getString("district"));
                                moradaModel.setNomeMorada(dataobj.getString("address_name"));
                                moradaArrayList.add(moradaModel);

                            }

                            for (int i = 0; i < moradaArrayList.size(); i++){
                                names.add(moradaArrayList.get(i).getNomeMorada().toString());
                                idsMoradas.add(moradaArrayList.get(i).getMoradaId());
                            }

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(ProductDetails.this, R.layout.simple_spinner_item, names);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            spinnerMorada.setAdapter(spinnerArrayAdapter);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //displaying the error in toast if occurrs
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", extraEmail);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
                headers.put("abc", "value");
                return headers;
            }
        };
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);
    }

    public void inicializeComponents(){

        nomeProduto = (TextView) findViewById(R.id.shop_produtoTvNomeProduto);
        categoriaProduto = (TextView) findViewById(R.id.shop_produtoTvCategoriaProduto);
        stockProduto = (TextView) findViewById(R.id.shop_produtoTvStockProduto);
        precoProduto = (TextView) findViewById(R.id.shop_produtoTvPrecoProduto);
        descricaoProduto = (TextView) findViewById(R.id.shop_produtoTvDescricaoProduto);
        fotoProduto = (ImageView) findViewById(R.id.shop_produtoFotoProduto);
        efetuarCompra = (Button)findViewById(R.id.shop_produtoAdicionar);

    }

    public void savePdf() {
        // Criar objeto Documento
        Document fatura = new Document();
        // Nome da fatura
        String nomeFatura = "fatura" + new SimpleDateFormat("HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        // Diretoria da fatura
        String diretoriaFatura = Environment.getExternalStorageDirectory() + "/" + nomeFatura + ".pdf";

        try {
            SharedPreferences prefs = getSharedPreferences("userInfo", MODE_PRIVATE);
            final String extraEmail = prefs.getString("email", "Nenhum email definido.");
            String faturaQuantidadeProduto = Integer.toString(contador);
            String faturaNomeProduto = nomeProduto.getText().toString().trim();
            String faturaPrecoProduto = precoProduto.getText().toString().trim();
            String faturaCodigoProduto = Integer.toString(_idProduto);
            int codigoProduto = Integer.parseInt(faturaCodigoProduto);
            int valorQuantidade = Integer.parseInt(faturaQuantidadeProduto);
            int valorPreco = Integer.parseInt(faturaPrecoProduto);
            int valorTotal = valorPreco * valorQuantidade;
            final int minReferencia = 000000000;
            final int maxReferencia = 999999999;
            final int random = new Random().nextInt((maxReferencia - minReferencia) + 1) + minReferencia;

            // Breakline
            LineSeparator breakLine = new LineSeparator();
            breakLine.setLineColor(new BaseColor(Color.argb(0,0,0,0)));

            // ESTRUTURA DA FATURA
            // Instanciar o documento PdfWriter
            PdfWriter.getInstance(fatura, new FileOutputStream(diretoriaFatura));
            // Abrir o documento para escrever e definir o tamanho da página
            fatura.open();
            fatura.setPageSize(PageSize.A5);
            // TÍTULO DA FATURA
            // Adicionar título
            // Criar chunks
            Chunk chunkTitulo = new Chunk("SURFECT");
            Chunk totalPagar = new Chunk("Total a pagar: " + valorTotal + "€");
            Chunk chunkRodape = new Chunk("The fusion between surf and perfection.");
            // Criar parágrafo do título
            Paragraph tituloParagrafo = new Paragraph(chunkTitulo);
            // Alinhamento do título
            tituloParagrafo.setAlignment(Element.ALIGN_CENTER);
            // Adicionar o chunk à fatura
            fatura.add(tituloParagrafo);
            // Adicionar breakline
            fatura.add(new Paragraph(""));
            fatura.add(new Chunk(breakLine));
            fatura.add(new Paragraph(""));
            // Adiocionar a data de criação, o criador e o autor.
            fatura.addCreationDate();
            fatura.addAuthor("© SURFECT 2020");
            fatura.addCreator("SURFECT");
            fatura.add(new Paragraph("Nome do produto: " + faturaNomeProduto));
            fatura.add(new Paragraph("Código: " + codigoProduto));
            fatura.add(new Paragraph("\n"));
            fatura.add(new Paragraph("Preço p/ item: " + faturaPrecoProduto + "€"));
            fatura.add(new Paragraph("Quantidade: " + valorQuantidade + " itens"));
            fatura.add(new Paragraph("\n"));
            Paragraph totalParagrafo = new Paragraph(totalPagar);
            totalParagrafo.setAlignment(Element.ALIGN_RIGHT);
            fatura.add(totalParagrafo);
            fatura.add(new Paragraph("\n"));
            fatura.add(new Paragraph("\n"));
            fatura.add(new Paragraph("A enviar para: "));
            fatura.add(new Paragraph("Morada: " + nomeMorada));
            fatura.add(new Paragraph("Email: " + extraEmail));
            fatura.add(new Paragraph("\n"));
            fatura.add(new Paragraph("\n"));
            fatura.add(new Paragraph("Pagamento"));
            fatura.add(new Paragraph("Entidade: 221100"));
            fatura.add(new Paragraph("Referência: " + random));
            fatura.add(new Paragraph("Valor: " + valorTotal + "€"));
            fatura.add(new Paragraph("\n"));
            fatura.add(new Paragraph("\n"));
            fatura.add(new Paragraph("Em caso de dúvida, contacte-nos."));
            fatura.add(new Paragraph("Email: surfect2020@gmail.com"));
            fatura.add(new Paragraph("\n"));
            fatura.add(new Paragraph("\n"));
            fatura.add(new Paragraph("\n"));
            fatura.add(new Paragraph("\n"));
            Paragraph rodape = new Paragraph(chunkRodape);
            rodape.setAlignment(Element.ALIGN_CENTER);
            fatura.add(rodape);

            fatura.close();
            Toast.makeText(this, "A fatura para o pagamento do produto foi baixada para o seu telemóvel. " + diretoriaFatura, Toast.LENGTH_SHORT).show();

        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void efetuarCompra() {

        // Procedimento para efetuar a compra de um produto
        final int _morada = idMorada;
        Toast.makeText(this, String.valueOf(idMorada), Toast.LENGTH_SHORT).show();
        SharedPreferences prefs = getSharedPreferences("userInfo", MODE_PRIVATE);
        final String extraEmail = prefs.getString("email", "Nenhum email definido.");

        if (_morada == 0){
            Toast.makeText(ProductDetails.this, "É obrigatório inserir a sua morada!", Toast.LENGTH_SHORT).show();
        }
        else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.URL_COMPRAS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.equals("")){
                                savePdf();//Toast.makeText(ProductDetails.this, response, Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(ProductDetails.this, response, Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ProductDetails.this, "Não atualizado! Tente outra vez! " + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("morada", String.valueOf(idMorada));
                    params.put("precoProduto", String.valueOf(_precoProduto));
                    params.put("tamanhoProduto",tamanhoSelecionado);
                    params.put("quantidadeProduto",String.valueOf(contador));
                    params.put("email", extraEmail);
                    params.put("produto", String.valueOf(_idProduto));
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(ProductDetails.this.getApplicationContext());
            requestQueue.add(stringRequest);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    savePdf();
                }
                else {
                    Toast.makeText(this, "Permissão negada. Por favor, garanta permissão.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         tamanhoSelecionado = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


