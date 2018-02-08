package com.btprinter.demo;

import android.util.Log;

import com.smartdevicesdk.btprinter.StringUtility;

public class PrintCommand {
	private static final String TAG = "PrintStyle";

	/**
	 * �������ַ���תbyte
	 */
	private static byte decodeBinaryString(String byteStr) {
		int re, len;
		if (null == byteStr) {
			return 0;
		}
		len = byteStr.length();
		if (len != 4 && len != 8) {
			return 0;
		}
		if (len == 8) {// 8 bit����
			if (byteStr.charAt(0) == '0') {// ����
				re = Integer.parseInt(byteStr, 2);
			} else {// ����
				re = Integer.parseInt(byteStr, 2) - 256;
			}
		} else {// 4 bit����
			re = Integer.parseInt(byteStr, 2);
		}
		return (byte) re;
	}

	/**
	 * �����ַ��Ҽ��,���ַ�����Ϊ: n*0.125mm,���ַ��Ŵ�ʱ���Ҽ����֮�Ŵ���ͬ�ı���
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_CharSpace_Right(int n) {
		if (n < 0 || n > 255) {
			return null;
		}
		CMD.ESC_margin_right[2] = (byte) n;

		return CMD.ESC_margin_right;
	}

	/**
	 * ѡ���ַ���ӡģʽ
	 * 
	 * @param font
	 *            0,24�����ֺ�; 1,16�����ֺ�
	 * @param blod
	 *            1,�Ӵ�
	 * @param doubleHeight
	 *            1���߶ȼӱ�
	 * @param doubleWidth
	 *            1����ȼӱ�
	 * @param underLine
	 *            1���»���
	 * @return
	 */
	public static byte[] set_FontStyle(int font, int blod, int doubleHeight,
			int doubleWidth, int underLine) {
		if (font < 0 | font > 1 | blod < 0 | blod > 1 | doubleHeight < 0
				| doubleWidth > 1 | underLine < 0 | underLine > 1) {
			return null;
		}
		/*String str = font + "00" + blod + doubleHeight + doubleWidth + "0"
				+ underLine;*/
		String str = underLine+"0"+doubleWidth+doubleHeight+blod+"00" +font;
		byte bt = decodeBinaryString(str);
		CMD.ESC_font_style[2] = bt;
		Log.d(TAG, StringUtility.ByteArrayToString(CMD.ESC_font_style,
				CMD.ESC_font_style.length));
		return CMD.ESC_font_style;
	}

	/**
	 * ���þ��Դ�ӡλ��
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_Absolute_Point(int n) {
		if (n < 0 || n > 65535) {
			return null;
		}
		CMD.ESC_absolute[2] = (byte) (n & 0xff);
		CMD.ESC_absolute[3] = (byte) (n >>> 8);
		return CMD.ESC_absolute;
	}

	/**
	 * ѡ��/ȡ���»���ģʽ
	 * 
	 * @param n
	 *            0��ȡ����1��1���2��2���
	 * @return
	 */
	public static byte[] set_UnderLineMode(int n) {
		if (n < 0 | n > 2) {
			return null;
		}
		CMD.ESC_underline[2] = (byte) n;
		return CMD.ESC_underline;
	}

	/**
	 * �����м��
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_LineSpace(int n) {
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.ESC_linespace[2] = (byte) n;
		return CMD.ESC_linespace;
	}

	/**
	 * ���Ʒ�������ʾ�� <br/>
	 * ˵���� nֵΪ���������еĴ����� <br/>
	 * tֵΪ������ÿ�����е�ʱ�䣬ʱ��Ϊ(t �� 50)ms��
	 * 
	 * @param n
	 * @param t
	 * @return
	 */
	public static byte[] set_Buzzer(int n, int t) {
		CMD.ESC_buzzer[2] = (byte) n;
		CMD.ESC_buzzer[3] = (byte) t;
		return CMD.ESC_buzzer;
	}

	/**
	 * ���ܣ����Ʒ�������ʾ, ͬʱ��������˸��<br/>
	 * ˵������mֵΪ���������еĴ���, ͬʱҲ��ָʾ����˸�Ĵ�����<br/>
	 * ��tֵΪ������ÿ�����е�ʱ�䣬ʱ��Ϊ(t �� 50)ms��<br/>
	 * ��nֵΪָʾ��ÿ�γ�����ʱ�䣬ʱ��Ϊ(t �� 50)ms��<br/>
	 * 
	 * @param m
	 * @param t
	 * @param n
	 * @return
	 */
	public static byte[] set_Buzzer_LED(int m, int t, int n) {
		CMD.ESC_buzzer_led[2] = (byte) m;
		CMD.ESC_buzzer_led[3] = (byte) t;
		CMD.ESC_buzzer_led[4] = (byte) n;
		return CMD.ESC_buzzer_led;
	}

	/**
	 * ���ܣ�����nȡֵѡ���ȡ���Ӵ�ģʽ��<br/>
	 * ˵������0 �� n �� 255����ֻ��n�����λ��Ч��<br/>
	 * �������λΪ0ʱ��ȡ���Ӵ�ģʽ��<br/>
	 * �������λΪ1ʱ��ѡ��Ӵ�ģʽ��<br/>
	 * ��ESC ! ͬ������ѡ��/ȡ���Ӵ�ģʽ�������յ�������Ч��<br/>
	 * ��nĬ��Ϊ0��
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_Blod(int n) {
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.ESC_font_blod[2] = (byte) n;
		return CMD.ESC_font_blod;
	}

	/**
	 * ���ܣ�����nѡ��/ȡ��˫�ش�ӡģʽ��<br/>
	 * ˵������0 �� n �� 255����ֻ��n�����λ��Ч��<br/>
	 * �������λΪ0ʱ��ȡ��˫�ش�ӡģʽ��<br/>
	 * �������λΪ1ʱ��ѡ��˫�ش�ӡģʽ��<br/>
	 * ����������Ӵִ�ӡЧ����ͬ;<br/>
	 * ��nĬ��Ϊ0��
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_Double(int n) {
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.ESC_double_print[2] = (byte) n;
		return CMD.ESC_double_print;
	}

	/**
	 * ���ܣ���ӡ���������ݲ���ֽ n���У�0��n��255��<br/>
	 * ˵��������ӡ�����󣬽���ǰ��ӡλ���������ף�
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_PrintAndFeed(int n) {
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.ESC_print_feed[2] = (byte) n;
		return CMD.ESC_print_feed;
	}

	/**
	 * ���ܣ�����nֵѡ�����壬nֵ��ȡ��0��1��48��49��<br/>
	 * ˵������nֵ��Ӧ�������±�<br/>
	 * n ����<br/>
	 * 0 ѡ��24�����ֺ�<br/>
	 * 1 ѡ��16�����ֺ�<br/>
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_FontSize(int n) {
		if (n < 0 | n > 1) {
			return null;
		}
		CMD.ESC_font_size[2] = (byte) n;
		return CMD.ESC_font_size;
	}

	/**
	 * ���ܣ�����nֵѡ�����壬nֵ��ȡ��0��1��48��49��<br/>
	 * ˵������nֵ��Ӧ�������±� <br/>
	 * ˵��<br/>
	 * 1B 4E 00 00 �ָ���������<br/>
	 * 1B 4E 02 m ���ô��ڲ����ʣ�ȡֵ��Χ1~8, Ĭ��m=6, ������230400��<br/>
	 * m=1:������9600 m=2:������19200<br/>
	 * m=3:������38400 m=4:������57600<br/>
	 * m=5 ������115200 m=6:������230400<br/>
	 * m=7 ������460800 m=8:������921600<br/>
	 * <br/>
	 * 1B 4E 04 m ���ô�ӡŨ�ȼ���ȡֵ��Χ0~9, Ĭ��m=0��<br/>
	 * m =1:��ӡŨ�ȼ���1<br/>
	 * m =2:��ӡŨ�ȼ���2<br/>
	 * m =3:��ӡŨ�ȼ���3<br/>
	 * m =4:��ӡŨ�ȼ���4<br/>
	 * ��<br/>
	 * m =9:��ӡŨ�ȼ���9<br/>
	 * <br/>
	 * 1B 4E 05 m ���ô���ҳ��Ĭ��m=15 CP_936, �������ģ�<br/>
	 * m ��ֵ�� [ESC t n]ָ���е�nֵ������ͬ.
	 * 
	 * @param n
	 * @param m
	 * @return
	 */
	public static byte[] set_Setting_Parameter(int n, int m) {
		CMD.ESC_setting_parameter_save[2] = (byte) n;
		CMD.ESC_setting_parameter_save[3] = (byte) m;
		return CMD.ESC_setting_parameter_save;
	}

	/**
	 * ���ܣ������ַ������ӡ<br/>
	 * ˵������nֵ��Ӧ�������±�<br/>
	 * n ����<br/>
	 * 1 �ַ�������<br/>
	 * 2 �ַ���ȷŴ�����<br/>
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_FontDoubleWidth(int n) {
		if (n < 1 | n > 2) {
			return null;
		}
		CMD.ESC_double_width[2] = (byte) n;
		return CMD.ESC_double_width;
	}

	/**
	 * ���ܣ������ַ������ߴ�ӡ<br/>
	 * ˵������nֵ��Ӧ�������±�<br/>
	 * n ����<br/>
	 * 1 �ַ�����������<br/>
	 * 2 �ַ���ȸ߶ȶ��Ŵ�����<br/>
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_FontDouble(int n) {
		if (n < 1 | n > 2) {
			return null;
		}
		CMD.ESC_double_width_height[2] = (byte) n;
		return CMD.ESC_double_width_height;
	}

	/**
	 * ���ܣ����ú������λ��,<br/>
	 * ˵�������������ӡλ�����õ��൱ǰλ��( nL + nH��256)���� 0��nL��255��0��nH��255�� <br/>
	 * �������ɴ�ӡ��������ý������ԣ�<br/>
	 * ������ӡλ�������ƶ�ʱ��nL+ nH��256 = N��<br/>
	 * ����ӡ��ʼλ�ôӵ�ǰλ���ƶ���N��λ�ô�.<br/>
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_HorizontalPoint(int n) {
		if (n < 0 || n > 65535) {
			return null;
		}
		CMD.ESC_x_point[2] = (byte) (n & 0xff);
		CMD.ESC_x_point[3] = (byte) (n >>> 8);
		return CMD.ESC_x_point;
	}

	/**
	 * ���ܣ�ʹ���еĴ�ӡ���ݰ�ĳһָ�����뷽ʽ���С�<br/>
	 * ˵������0��n ��2��48��n ��50, nĬ��Ϊ0��ȡֵ����뷽ʽ��Ӧ��ϵ����:<br/>
	 * 
	 * n ���뷽ʽ<br/>
	 * 0,48 �����<br/>
	 * 1,49 �м����<br/>
	 * 2,50 �Ҷ���<br/>
	 * 
	 * ��������ֻ��������Ч��<br/>
	 * ���������ڴ�ӡ����ִ�ж��룻 <br/>
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_Align(int n) {
		if (n < 0 | n > 2) {
			return null;
		}
		CMD.ESC_align[2] = (byte) n;
		return CMD.ESC_align;
	}

	/**
	 * ��ӡ����ǰ��ֽn�ַ���<br/>
	 * ���ܣ���ӡ������������ݲ���ǰ��ֽn�ַ��У� 0��n��255��<br/>
	 * ˵�������������ӡ���Ĵ�ӡ��ʼλ�����������ף�<br/>
	 * �������Ӱ����ESC 2 �� ESC 3���õ��м�ࣻ<br/>
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_PrintAndFeedLine(int n) {
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.ESC_print_feed_line[2] = (byte) n;
		return CMD.ESC_print_feed_line;
	}

	/**
	 * ���ô���ҳ<br/>
	 * ����Χ�� 0 �� n ��128<br/>
	 * �������� ���ַ��������ѡ��ҳn<br/>
	 * n ����ҳ<br/>
	 * 0 PC437 [������ŷ�ޱ�׼]<br/>
	 * 2 PC850 [������, ��ŷ��]<br/>
	 * 3 PC860 [��������]<br/>
	 * 4 PC863 [���ô�-����]<br/>
	 * 5 PC865 [��ŷ- ����ն�����]<br/>
	 * 6 PC1252 [West Europe]<br/>
	 * 7 PC737 [Greek]<br/>
	 * 8 PC862 [Hebrew]<br/>
	 * 11 CP775 [���޵ĺ���]<br/>
	 * 13 CP949 [����]<br/>
	 * 14 CP950 [��������]<br/>
	 * 15 CP936 [��������]<br/>
	 * 16 PC1252<br/>
	 * 17 PC866 [Cyrillice*2]<br/>
	 * 18 PC852 [Latin2]<br/>
	 * 19 PC858 [��ŷ��]<br/>
	 * 21 CP866 [˹������/����]<br/>
	 * 22 CP855 [˹������ ��������]<br/>
	 * 23 CP857 [��������] <br/>
	 * 24 CP864 [��������]<br/>
	 * 34 CP1251[������� ˹������ ����]<br/>
	 * 35 CP1252[��ŷ(������I)]<br/>
	 * 36 CP1253[ϣ����]<br/>
	 * 37 CP1254[��������]<br/>
	 * 38 CP1255[ϣ������]<br/>
	 * 39 CP1256[��������]<br/>
	 * 40 CP1257[���޵ĺ���]
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_CodePage(int n) {
		if (n < 0 | n > 40) {
			return null;
		}
		CMD.ESC_codepage[2] = (byte) n;
		return CMD.ESC_codepage;
	}

	/**
	 * ѡ��/ȡ�����ô�ӡģʽ<br/>
	 * ���ܣ�����nֵѡ���ȡ�����ô�ӡģʽ��0 �� n �� 255��nֵֻ�����λ��Ч��<br/>
	 * ˵��������n�����λΪ0ʱ��ȡ�����ô�ӡģʽ��<br/>
	 * ����n�����λΪ1ʱ��ѡ���ô�ӡģʽ��<br/>
	 * ��nĬ��ֵΪ0��
	 * 
	 * @param n
	 * @return
	 */
	public static byte[] set_Handstand(int n) {
		if (n < 0 | n > 1) {
			return null;
		}
		CMD.ESC_handstand[2] = (byte) n;
		return CMD.ESC_handstand;
	}
	
	/**
	 * ����QrCode��ά�붥���հ׸߶�<br/>
	 * ���ܣ�����QrCode��ά�붥���հ׸߶�<br/>
˵��:  n��ȡֵ��Χ 0 <= n <= 255,  nĬ��ȡֵ24
	 * @param n
	 * @return
	 */
	public static byte[] set_QrCode_TopSpace(int n){
		if (n < 0 | n > 255) {
			return null;
		}
		CMD.US_qrcode_top_space[2]=(byte) n;
		return CMD.US_qrcode_top_space;
	}
}
