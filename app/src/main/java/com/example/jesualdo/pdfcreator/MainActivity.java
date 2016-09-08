package com.example.jesualdo.pdfcreator;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String NOMBRE_CARPETA_APP = "PDFCreator";
    private static final String GENERADOS = "PDFs Generados";

    Button btn_generar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_generar = (Button) findViewById(R.id.btn_generarPDF);
        btn_generar.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                generarPDFOnClick();
            }
        });
    }

    public void generarPDFOnClick(){
        //Se crea un documento tamaño carta
        Document document = new Document(PageSize.LETTER);

        //Se crea el nombre del archivo con una fecha de creación
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String NOMBRE_ARCHIVO = "Documento[" + timeStamp + "].pdf";

        Log.v("PDFCREADOR",this.getFilesDir().toString());

        //Se obtiene el directorio donde se creará la carpeta para los documentos.
        String raiz = Environment.getExternalStorageDirectory().toString();
        //Se crea la instancia de un nuevo directorio en el directorio obtenido.
        File pdfDir = new File(raiz + File.separator + NOMBRE_CARPETA_APP);
        //Se verifica si el directorio existe.Si no existe, debe crearse.
        if(!pdfDir.exists()){
            pdfDir.mkdir();
        }

        //Se inicia un subdirectorio, llamado "Reportes".
        File pdfSubDir = new File(pdfDir.getPath() + File.separator + GENERADOS);
        //Se verifica si el subdirectorio existe.Si no existe, debe crearse.
        if(!pdfSubDir.exists()){
            pdfSubDir.mkdir();
        }

        //Se crea el nombre completo del archivo. Raíz/Directorio/Subdirectorio/Archivo
        String nombre_completo = raiz + File.separator + NOMBRE_CARPETA_APP + File.separator +
                GENERADOS + File.separator + NOMBRE_ARCHIVO;

        //Se genera un archivo de salida con el nombre creado.
        File outputfile = new File(nombre_completo);
        //Se verifica si el archivo existe.Si no existe, debe crearse.
        if(outputfile.exists()){
            outputfile.delete();
        }

        //Se comienza a escribir sobre el archivo.
        try {
            //Se crea una instancia (singleton) de PdfWriter para crear el documento.
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(nombre_completo));

            //Se crea el documento y se escriben sus propiedades
            document.open();
            document.addAuthor("Jesualdo");
            document.addCreator("Jesualdo");
            document.addSubject("Creacion de un PDF");
            document.addCreationDate();
            document.addTitle("Creación de un PDF");

            //Se crea una instancia (singleton) de XMLWorder para escribir sobre el archivo con HTML.
            XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
            //Se escribe un código HTML
            String htmlToPDF = "<html> <head> </head> <body> <h1> Hola que tal </h1> <p> Este es un párrafo </p> </body></html>";
            try {
                //El código HTML se transforma y se escribe en el documento.
                worker.parseXHtml(pdfWriter, document, new StringReader(htmlToPDF));
                //Se cierra el archivo y se manda una confirmación.
                document.close();
                Toast.makeText(this, "El PDF esta generado", Toast.LENGTH_LONG).show();
                //Se muestra el archivo al usuario.
                muestraPDF(nombre_completo, this);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void muestraPDF(String archivo, Context context){
        //Se crea un archivo con el nombre completo creado. Raíz/Directorio/Subdirectorio/Archivo.pdf
        File file = new File(archivo);
        Toast.makeText(context, "Leyendo el archivo", Toast.LENGTH_LONG).show();
        //Se crea un intent para abrir un archivo tipo PDF, de manera que otra aplicación la abra.
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/pdf");
        //Se borra el caché que pudiera existir.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Se crea una nueva tarea por si hay un PDF abierto.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            //Se muestra la nueva actividad que despliega el PDF.
            context.startActivity(intent);
        }catch (ActivityNotFoundException e){
            //Si no existe un app para abrir este tipo de archivo, se notifica al usaurio.
            Toast.makeText(context, "No tiene un app para abrir este tipo de datos", Toast.LENGTH_LONG).show();
        }
    }
}