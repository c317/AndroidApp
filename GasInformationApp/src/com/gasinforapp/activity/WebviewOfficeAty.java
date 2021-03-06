package com.gasinforapp.activity;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.w3c.dom.Document;

import com.example.gasinformationapp_101.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebviewOfficeAty extends Activity {

	private String docPath =  "/storage/emulated/0/down/";//"/mnt/sdcard/documents/";
	private String docName = "1.doc";
	private String savePath =  "/storage/emulated/0/down/";//"/mnt/sdcard/documents/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_office);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		docPath = bundle.getString("path");
		savePath = bundle.getString("path");
		docName = bundle.getString("fileName");
		
		String name = docName.substring(0, docName.indexOf("."));
		try {
			if (!(new File(savePath + name).exists()))
				new File(savePath + name).mkdirs();
			convert2Html(docPath + docName, savePath + name + ".html");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// WebView加载显示本地html文件
		WebView webView = (WebView) this.findViewById(R.id.office);
		WebSettings webSettings = webView.getSettings();
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webView.loadUrl("file://" + savePath + name + ".html");
	}

	/**
	 * word文档转成html格式
	 * */
	public void convert2Html(String fileName, String outPutFile)
			throws TransformerException, IOException,
			ParserConfigurationException {
		HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(
				fileName));
		WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
				DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.newDocument());

		// 设置图片路径
		wordToHtmlConverter.setPicturesManager(new PicturesManager() {
			public String savePicture(byte[] content, PictureType pictureType,
					String suggestedName, float widthInches, float heightInches) {
				String name = docName.substring(0, docName.indexOf("."));
				return name + "/" + suggestedName;
			}
		});

		// 保存图片
		List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
		if (pics != null) {
			for (int i = 0; i < pics.size(); i++) {
				Picture pic = (Picture) pics.get(i);
				System.out.println(pic.suggestFullFileName());
				try {
					String name = docName.substring(0, docName.indexOf("."));
					pic.writeImageContent(new FileOutputStream(savePath + name
							+ "/" + pic.suggestFullFileName()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		wordToHtmlConverter.processDocument(wordDocument);
		Document htmlDocument = wordToHtmlConverter.getDocument();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DOMSource domSource = new DOMSource(htmlDocument);
		StreamResult streamResult = new StreamResult(out);

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty(OutputKeys.METHOD, "html");
		serializer.transform(domSource, streamResult);
		out.close();
		// 保存html文件
		writeFile(new String(out.toByteArray()), outPutFile);
	}

	/**
	 * 将html文件保存到sd卡
	 * */
	public void writeFile(String content, String path) {
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
			bw.write(content);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fos != null)
					fos.close();
			} catch (IOException ie) {
			}
		}
	}
}
