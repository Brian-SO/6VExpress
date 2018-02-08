package com.btprinter.demo;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smartdevicesdk.btprinter.BluetoothService;
import com.smartdevicesdk.btprinter.PrintService;
import com.smartdevicesdk.btprinter.ICoallBack;
import com.smartdevicesdk.btprinter.PrinterInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PrintTestActivity extends Activity implements OnClickListener {

	protected static final String TAG = "PrintTestActivity";

	EditText editText_input;
	CheckBox checkedTextView_ticket;
	Spinner spinner_language, spinner_imagetype;
	Button button_printtext, button_printunicode, button_printimage,
			button_printbarcode, button_printqrcode, button_printticket,
			button_connect, button_checkstatus, button_relink;
	ListView listView_cmd;
	static TextView textView_msg;
	LinearLayout linearlayout_printer;
	boolean hasCmdFile = false;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	public static Handler mhandler;

	Context mContext;

	String macAddressStr = "";

	/**
	 * ͼƬ��ӡ����
	 */
	int imageType = 0;
	final String[] imageTypeArray = new String[] { "RASTER", "Discrete",
			"GRAY", "POINT" };
	private static final int IMAGE = 101;

	PrintService pl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_printtest);
		mContext = this;
		findViewById();
		Intent intent = getIntent();
		int position = intent.getIntExtra("position", 0);

		// ����esc/popָ���б�
		List<String> list = ReadTxtFile("/sdcard/cmd.txt");
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, getData(list),
				android.R.layout.simple_list_item_2, new String[] { "title",
						"description" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		listView_cmd.setAdapter(simpleAdapter);
		listView_cmd
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Map map = (Map) listView_cmd
								.getItemAtPosition(position);
						String text = map.get("title").toString();
						String cmd = map.get("description").toString();
						byte[] bt = hexStringToBytes(cmd);
						if (pl.write(bt)) {
							Toast.makeText(PrintTestActivity.this,
									text + ",���ͳɹ�Succeed", Toast.LENGTH_SHORT).show();
						}
					}
				});

		pl = new PrintService(this);
		pl.setOnPrinterStatus(new ICoallBack() {
			@Override
			public void onPrinterStatus(int s, String text) {
				switch (s) {
				case BluetoothService.STATE_CONNECTED:
					// ���ӳɹ�����������
					pl.write(PrintCommand.set_Buzzer(2, 1));
					
					// ���ӳɹ�
					linearlayout_printer.setVisibility(View.VISIBLE);
					text += "\r\n"
							+ getResources().getString(R.string.str_succonnect);
					button_connect.setText(getResources().getString(
							R.string.button_disconect));
					button_relink.setVisibility(View.GONE);

					macAddressStr = pl.getMacAddress();
					text += "\r\n" + macAddressStr;

					break;
				case BluetoothService.STATE_CONNECTING:
					// ��������
					text += "\r\n"
							+ getResources().getString(R.string.str_connecting);

					break;
				case BluetoothService.STATE_LISTEN:
				case BluetoothService.STATE_NONE:
					// δ����
					linearlayout_printer.setVisibility(View.INVISIBLE);
					text += "\r\n"
							+ getResources()
									.getString(R.string.str_unconnected);
					button_connect.setText(getResources().getString(
							R.string.button_connect));

					// ����������ť
					if (macAddressStr == "") {
						button_relink.setVisibility(View.GONE);
					} else {
						button_relink.setVisibility(View.VISIBLE);
					}
					break;
				case BluetoothService.READ_DATA:
					// ��ӡ����������

					break;
				}
				Log.d(TAG, text);
				ShowMsg(text);
			}
		});

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pl.disconnect();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case IMAGE:
			// ��ȡͼƬ·��
			String imagePath = null;
			if (data != null) {
				Uri selectedImage = data.getData();
				String[] filePathColumns = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage,
						filePathColumns, null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePathColumns[0]);
				imagePath = c.getString(columnIndex);
				c.close();

				Bitmap btMap = BitmapFactory.decodeFile(imagePath);
				if (imageTypeArray[imageType].equals("POINT")) {
					// pl.printImage(btMap);
				} else if (imageTypeArray[imageType].equals("GRAY")) {
					pl.printGrayImage(btMap);
				} else if (imageTypeArray[imageType].equals("RASTER")) {
					pl.printRasterImage(btMap);
				} else if (imageTypeArray[imageType].equals("Discrete")) {
					// pl.printDiscreteImage(btMap);
				}
			}
			break;
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				pl.connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				pl.connectDevice(data, false);
			}
			break;
		
		default:
			break;
		}

	}

	private void findViewById() {
		Button button_printtext = (Button) findViewById(R.id.button_printtext);
		button_printtext.setOnClickListener(this);
		Button button_printunicode = (Button) findViewById(R.id.button_printunicode);
		button_printunicode.setOnClickListener(this);
		Button button_printimage = (Button) findViewById(R.id.button_printimage);
		button_printimage.setOnClickListener(this);
		Button button_printbarcode = (Button) findViewById(R.id.button_printbarcode);
		button_printbarcode.setOnClickListener(this);
		Button button_printqrcode = (Button) findViewById(R.id.button_printqrcode);
		button_printqrcode.setOnClickListener(this);
		Button button_printticket = (Button) findViewById(R.id.button_printticket);
		button_printticket.setOnClickListener(this);
		button_connect = (Button) findViewById(R.id.button_connect);
		button_connect.setOnClickListener(this);
		button_relink = (Button) findViewById(R.id.button_relink);
		button_relink.setOnClickListener(this);

		Button button_checkstatus = (Button) findViewById(R.id.button_checkstatus);
		button_checkstatus.setOnClickListener(this);

		linearlayout_printer = (LinearLayout) findViewById(R.id.linearlayout_printer);
		linearlayout_printer.setVisibility(View.GONE);

		listView_cmd = (ListView) findViewById(R.id.listView_cmd);
		editText_input = (EditText) findViewById(R.id.editText_input);
		checkedTextView_ticket = (CheckBox) findViewById(R.id.checkedTextView_ticket);
		spinner_language = (Spinner) findViewById(R.id.spinner_language);
		spinner_imagetype = (Spinner) findViewById(R.id.spinner_imagetype);
		textView_msg = (TextView) findViewById(R.id.textView_msg);
		textView_msg.setMovementMethod(ScrollingMovementMethod.getInstance());

		editText_input.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					editText_input.selectAll();
				}
			}
		});

		spinner_imagetype.setAdapter(new ArrayAdapter<String>(mContext,
				android.R.layout.simple_spinner_item, imageTypeArray));

		spinner_imagetype
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						imageType = position;
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		SpinnerAdapterLanguage adapter = new SpinnerAdapterLanguage(mContext,
				android.R.layout.simple_spinner_item, getData());
		spinner_language.setAdapter(adapter);

		spinner_language
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						LanguageModel map = (LanguageModel) spinner_language
								.getItemAtPosition(position);
						String languageStr = map.language;
						// ��������
						String description = map.description;
						// ����ָ��
						int code = map.code;
						Log.d(TAG, "onItemClick: spinner_language="
								+ description + "," + code);


						pl.setPrint_Language(languageStr,code);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

		editText_input.setText("����һ�δ�ӡ��������\r\nThis is a print test text\r\n\r\n");// ���ʤ���\r\n
	}

	public void ShowMsg(String msg) {
		msg = msg.trim();
		if (!msg.equals("")) {
			if (textView_msg != null) {
				textView_msg.append("\r\n"+msg);
				
				int offset=textView_msg.getLineCount()*textView_msg.getLineHeight();
		        if(offset>textView_msg.getHeight()){
		        	textView_msg.scrollTo(0,offset-textView_msg.getHeight());
		        }
			}
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		}
	}

	private String getInputText() {
		final String textStr = editText_input.getText().toString();
		if (textStr.isEmpty()) {
			ShowMsg("����������\r\nPlease enter content");
		}
		return textStr;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_printtext:// ����
			if (!hasCmdFile) {
				printTestInfo();
			} else {
				pl.printText(getInputText() + "\r\n");
				if (checkedTextView_ticket.isChecked()) {
					pl.write(new byte[] { 0x0c });
				}
			}
			break;
		case R.id.button_printunicode:// Unicode
			// PrintService.pl().printUnicode(textStr);
			pl.printUnicode_1F30(getInputText());
			if (checkedTextView_ticket.isChecked()) {
				pl.write(new byte[] { 0x0c });
			}
			break;
		case R.id.button_printimage:// ѡ�񱾻�ͼƬ
			// �������
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, IMAGE);

			break;
		case R.id.button_printbarcode:// ����
			String textStr = getInputText();
			if (!isASCII(textStr)) {
				ShowMsg("�������ݱ�����(Ӣ�ġ����֡�����)ASCII\r\nThe barcode content must be ASCII");
			} else {
				pl.printBarCode(textStr);
				if (checkedTextView_ticket.isChecked()) {
					pl.write(new byte[] { 0x0c });
				}
			}
			break;
		case R.id.button_printqrcode:// ��ά��
			new Thread(new Runnable() {
				@Override
				public void run() {
					pl.write(PrintCommand.set_QrCode_TopSpace(0));
					pl.printQrCode(getInputText());
					if (checkedTextView_ticket.isChecked()) {
						pl.write(new byte[] { 0x0c });
					}
				}
			}).start();
			break;
		case R.id.button_printticket:// СƱ
			pl.printText(GetPrintStr());
			break;
		case R.id.button_connect:// ���Ӵ�ӡ��
			if (pl.isConnected()) {
				pl.disconnect();
			} else {
				Intent serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent,
						REQUEST_CONNECT_DEVICE_SECURE);
			}
			break;

		case R.id.button_checkstatus:
			PrinterInfo pi = pl.getPrinterInfo();
			if (pi.getPaper() == 0) {
				ShowMsg(getResources().getString(R.string.tip_print_normal));
			} else {
				ShowMsg(getResources().getString(R.string.tip_print_nopaper));
			}
			break;

		case R.id.button_relink:
			if (macAddressStr != "") {
				pl.connectDevice(macAddressStr);
			}
			break;
		default:
			break;
		}

	}

	public static boolean isASCII(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 32 || chr > 127)
				return false;
		}
		return true;
	}

	public void printTestInfo() {

		pl.write(PrintCommand.set_FontStyle(0, 0, 0, 0, 0));
		pl.printText("24������\r\n");

		pl.write(PrintCommand.set_FontStyle(0, 0, 0, 0, 1));
		pl.printText("24�������»���\r\n");

		pl.write(PrintCommand.set_FontStyle(0, 1, 0, 0, 0));
		pl.printText("24������Ӵ�\r\n");

		pl.write(PrintCommand.set_FontStyle(0, 0, 0, 1, 0));
		pl.printText("24�������ȼӱ�\r\n");

		pl.write(PrintCommand.set_FontStyle(0, 0, 1, 0, 0));
		pl.printText("24������߶ȼӱ�\r\n");

		pl.write(PrintCommand.set_FontStyle(0, 0, 1, 1, 0));
		pl.printText("24�������߼ӱ�\r\n\r\n");

		pl.write(PrintCommand.set_FontStyle(1, 0, 0, 0, 0));
		pl.printText("16������\r\n");

		pl.write(PrintCommand.set_FontStyle(1, 0, 0, 0, 1));
		pl.printText("16�������»���\r\n");

		pl.write(PrintCommand.set_FontStyle(1, 1, 0, 0, 0));
		pl.printText("16������Ӵ�\r\n");

		pl.write(PrintCommand.set_FontStyle(1, 0, 0, 1, 0));
		pl.printText("16�������ȼӱ�\r\n");

		pl.write(PrintCommand.set_FontStyle(1, 0, 1, 0, 0));
		pl.printText("16������߶ȼӱ�\r\n");

		pl.write(PrintCommand.set_FontStyle(1, 0, 1, 1, 0));
		pl.printText("16�������߼ӱ�\r\n\r\n");

		pl.write(CMD.ESC_init);

		String barcodeStr = System.currentTimeMillis() + "";
		pl.printBarCode(barcodeStr);
		pl.printQrCode(barcodeStr);

		pl.write(PrintCommand.set_PrintAndFeedLine(2));
	}

	public String GetPrintStr() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		String date = sDateFormat.format(new java.util.Date());
		StringBuilder sb = new StringBuilder();

		String tou = "�ĺ�һ�ң�������չ㳡�꣩";
		String address = "��ɽ�����ϴ��9028��������չ㳡3¥���ࣨ1��������֮��վA���ڣ�";
		String saleID = "2016930233330";
		String item = "��Ŀ";
		Double price = 25.00;
		int count = 10;
		Double total = 0.00;
		Double fukuan = 500.00;

		sb.append("   " + tou + "     \n");
		sb.append("����:" + date + "  " + "\n����:" + saleID + "\n");
		sb.append("******************************\n");
		sb.append("��Ŀ" + "\t" + "����" + "\t" + "����" + "\t" + "С��" + "\n");
		for (int i = 0; i < count; i++) {
			Double xiaoji = (i + 1) * price;
			sb.append(item + (i + 1) + "\t" + (i + 1) + "\t" + price + "\t"
					+ xiaoji);
			total += xiaoji;

			if (i != (count))
				sb.append("\n");
		}

		sb.append("******************************\n");
		sb.append("����: " + count + " �ϼ�:   " + total + "\n");
		sb.append("����: �ֽ�" + "    " + fukuan + "\n");
		sb.append("�ֽ�����:" + "   " + (fukuan - total) + "\n");
		sb.append("******************************\n");
		sb.append("��ַ��" + address + "\n");
		sb.append("�绰��0755-89829988\n");

		sb.append("******лл�ݹ˻�ӭ�´ι���******\r\n\n\n");
		return sb.toString();
	}

	/**
	 * ����SimpleAdapter�ĵڶ�������������ΪList<Map<?,?>>
	 * 
	 * @param cmdStr
	 * @return
	 */
	private List<Map<String, String>> getData(List<String> cmdStr) {
		List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
		Resources res = getResources();
		for (int i = 0; i < cmdStr.size(); i++) {
			String[] cmdArray = cmdStr.get(i).split(",");
			if (cmdArray.length == 2) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", cmdArray[0]);
				map.put("description", cmdArray[1]);
				listData.add(map);
			}
		}
		return listData;
	}

	/**
	 * ���ַ�����ʽ��ʾ��ʮ��������ת��Ϊbyte����
	 */
	public static byte[] hexStringToBytes(String hexString) {
		hexString = hexString.toLowerCase();
		String[] hexStrings = hexString.split(" ");
		byte[] bytes = new byte[hexStrings.length];
		for (int i = 0; i < hexStrings.length; i++) {
			char[] hexChars = hexStrings[i].toCharArray();
			bytes[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
		}
		return bytes;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789abcdef".indexOf(c);
	}

	public List<String> ReadTxtFile(String strFilePath) {
		List<String> list = new ArrayList<>();
		String path = strFilePath;
		String content = ""; // �ļ������ַ���
		// ���ļ�
		File file = new File(path);
		// ���path�Ǵ��ݹ����Ĳ�����������һ����Ŀ¼���ж�
		if (!file.isFile()) {
			Log.d("TestFile", "The File doesn't not exist.");

			copyFile("cmd.txt", "/sdcard/cmd.txt");
			if (!new File(strFilePath).exists()) {
				Toast.makeText(this, getResources().getString(R.string.tip_cmdfile), Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			hasCmdFile = true;
		}

		try {
			InputStream instream = new FileInputStream(file);
			if (instream != null) {
				InputStreamReader inputreader = new InputStreamReader(instream,
						"UTF-8");
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				// ���ж�ȡ
				while ((line = buffreader.readLine()) != null) {
					// content += line + "\n";
					list.add(line.trim());
				}
				instream.close();
			}
		} catch (java.io.FileNotFoundException e) {
			Log.d("TestFile", "The File doesn't not exist.");
			Toast.makeText(this, getResources().getString(R.string.tip_cmdfile), Toast.LENGTH_SHORT)
					.show();
		} catch (IOException e) {
			Log.d("TestFile", e.getMessage());
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}

		return list;
	}

	private void copyFile(String filename, String newFileName) {
		AssetManager assetManager = this.getAssets();

		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(filename);
			out = new FileOutputStream(newFileName);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}

	}

	/**
	 * ����SimpleAdapter�ĵڶ�������������ΪList<Map<?,?>>
	 * 
	 * @param
	 * @return
	 */
	private List<LanguageModel> getData() {
		Resources res = getResources();
		String[] cmdStr = res.getStringArray(R.array.language);
		List<LanguageModel> languageModelList = new ArrayList<>();
		for (int i = 0; i < cmdStr.length; i++) {
			String[] cmdArray = cmdStr[i].split(",");
			if (cmdArray.length == 3) {
				LanguageModel languageModel = new LanguageModel();
				languageModel.code = Integer.parseInt(cmdArray[0]);
				languageModel.language = cmdArray[1];
				languageModel.description = cmdArray[1] + " " + cmdArray[2];
				languageModelList.add(languageModel);
			}
		}
		return languageModelList;
	}
}
