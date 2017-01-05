package net.somtic.busquedasengoogle2017;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    class BuscarGoogle extends AsyncTask<String, Void, String> {

        private ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            progreso = new ProgressDialog(MainActivity.this);
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setMessage("Accediendo a Google...");
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(String... palabras) {
            try {
                return resultadosGoogle(palabras[0]);
            } catch(Exception e) {
                cancel(true);
                Log.e("HTTP", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String res) {
            progreso.dismiss();
            salida.append(res + "\n");
        }


        @Override
        protected void onCancelled() {
            progreso.dismiss();
            salida.append("Error al conectar\n");
        }

    }

    private EditText entrada;
    private TextView salida;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        entrada = (EditText) findViewById(R.id.EditText01);
        salida = (TextView) findViewById(R.id.TextView01);

    }

    public void buscar(View view){
        String palabras = entrada.getText().toString();
        salida.append(palabras + "--");
        new BuscarGoogle().execute(palabras);
    }

    String resultadosGoogle(String palabras) throws Exception {
        String pagina = "", devuelve = "";
        URL url = new URL("https://www.google.es/search?hl=es&q=\""
                + URLEncoder.encode(palabras, "UTF-8") + "\"");
        HttpURLConnection conexion =
                (HttpURLConnection)url.openConnection();
        conexion.setRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
        if (conexion.getResponseCode()==HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conexion.getInputStream()));
            String linea = reader.readLine();
            while (linea != null) {
                pagina += linea;
                linea = reader.readLine();
            }
            reader.close();
            int ini = pagina.indexOf("Aproximadamente");
            if (ini != -1) {
                int fin = pagina.indexOf(" ", ini + 16);
                devuelve = pagina.substring(ini + 16, fin);
            } else {
                devuelve = "no encontrado";
            }
        } else {
            salida.append("ERROR: " +
                    conexion.getResponseMessage() + "\n");
        }
        conexion.disconnect();
        return devuelve;
    }
}