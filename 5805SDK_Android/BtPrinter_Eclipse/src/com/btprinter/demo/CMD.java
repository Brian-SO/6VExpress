package com.btprinter.demo;

public class CMD {
	private static byte ESC=0x1B;
	private static byte FS=0x1C;
	private static byte GS=0x1D;
	private static byte RS=0x1E;
	private static byte US=0x1F;
	
	private static byte SP=0x20;
	
	
	/**
	 * 设置字符倍宽打印
	 */
	public static byte[] ESC_double_width_print=new byte[]{ESC,0x0E};
	
	/**
	 * 取消字符倍宽打印
	 */
	public static byte[] ESC_double_width_print_cancel=new byte[]{ESC,0x14};
	
	/**
	 * 设置字符右间距
	 */
	public static byte[] ESC_margin_right=new byte[]{ESC,SP,0};
	
	
	/**
	 * 选择字符打印模式
	 */
	public static byte[] ESC_font_style=new byte[]{ESC,'!',0};
	
	/**
	 * 设置绝对打印位置
	 */
	public static byte[] ESC_absolute=new byte[]{ESC,'$',0,0};
	
	/**
	 * 选择/取消下划线模式
	 */
	public static byte[] ESC_underline=new byte[]{ESC,'-',0};
	
	/**
	 * 设置默认行间距
	 */
	public static byte[] ESC_linespace_default=new byte[]{ESC,0x32};
	
	/**
	 * 设置行间距
	 */
	public static byte[] ESC_linespace=new byte[]{ESC,0x33,0};
	
	/**
	 * 初始化打印机
	 */
	public static byte[] ESC_init=new byte[]{ESC,0x40};
	
	/**
	 * 控制蜂鸣器提示
	 */
	public static byte[] ESC_buzzer=new byte[]{ESC,'B',0,0};
	
	/**
	 * 控制蜂鸣器提示及指示灯闪烁
	 */
	public static byte[] ESC_buzzer_led=new byte[]{ESC,'C',0,0,0};
	
	/**
	 * 选择/取消加粗模式
	 */
	public static byte[] ESC_font_blod =new byte[]{ESC,'E',0};
	
	/**
	 * 选择/取消双重打印模式
	 */
	public static byte[] ESC_double_print=new byte[]{ESC,'G',0};
	
	/**
	 * 打印并走纸n点行
	 */
	public static byte[] ESC_print_feed=new byte[]{ESC,'J',0};
	
	/**
	 * 选择字号
	 */
	public static byte[] ESC_font_size=new byte[]{ESC,'M',0};
	
	/**
	 * 设置打印机参数并保存到Flash中
	 */
	public static byte[] ESC_setting_parameter_save=new byte[]{ESC,'N',0,0};
	
	/**
	 * 设置字符倍宽
	 */
	public static byte[] ESC_double_width=new byte[]{ESC,'U',0};
	
	/**
	 * 设置字符倍宽倍高
	 */
	public static byte[] ESC_double_width_height=new byte[]{ESC,'W',0};
	
	/**
	 * 设置相对横向打印位置
	 */
	public static byte[] ESC_x_point=new byte[]{ESC,0x5C,0,0};
	
	/**
	 * 选择对齐方式
	 */
	public static byte[] ESC_align=new byte[]{ESC,'a',0};
	
	/**
	 * 打印并向前走纸n字符行
	 */
	public static byte[] ESC_print_feed_line=new byte[]{ESC,'d',0};
	
	/**
	 * 打印机全切纸
	 */
	public static byte[] ESC_cut_all=new byte[]{ESC,'i'};
	
	/**
	 * 打印机半切纸
	 */
	public static byte[] ESC_cut_half=new byte[]{ESC,'m'};
	
	/**
	 * 设置代码页
	 */
	public static byte[] ESC_codepage=new byte[]{ESC,'t',0};
	
	/**
	 * 查询打印机状态
	 */
	public static byte[] ESC_print_status=new byte[]{ESC,'v'};
	
	/**
	 * 查询打印结果
	 */
	public static byte[] ESC_print_result=new byte[]{ESC,'w'};
	
	/**
	 * 选择/取消倒置打印模式
	 */
	public static byte[] ESC_handstand=new byte[]{ESC,'{',0};
	
	
	
	
	/*************************************FS指令********************************************/
	
	/**
	 * 设置字符模式
	 */
	public static byte[] FS_font_style=new byte[]{FS,'!',0};
	
	/**
	 * 设置字符下划线
	 */
	public static byte[] FS_underline =new byte[]{FS,'-',0};
	
	/**
	 * 选择/取消字符放大两倍打印
	 */
	public static byte[] FS_font_double=new byte[]{FS,'W',0};
	
	
	

	/*************************************GS指令********************************************/
	/**
	 * 选择字符大小
	 */
	public static byte[] GS_font_size=new byte[]{GS,'!',0};
	
	/**
	 * 选择/取消反白打印模式
	 */
	public static byte[] GS_highlight=new byte[]{GS,'B',0};
	
	/**
	 * 选择HRI字符的打印位置
	 */
	public static byte[] GS_hri_location=new byte[]{GS,'H',0};
	
	/**
	 * 设置左边距
	 */
	public static byte[] GS_left_space=new byte[]{GS,'L',0,0};
	
	/**
	 * 设置打印区域宽度
	 */
	public static byte[] GS_width_area=new byte[]{GS,'W',0,0};
	
	/**
	 * 选择条码高度
	 */
	public static byte[] GS_barcode_height=new byte[]{GS,'h',0};
	
	/**
	 * 选择条码模块宽度
	 */
	public static byte[] GS_barcode_width=new byte[]{GS,'w',0};
	
	
	
	
	

	/*************************************RS指令********************************************/
	/**
	 * 进入休眠模式
	 */
	public static byte[] RS_sleep=new byte[]{RS,0x01};
	
	/**
	 * 设置自动进入休眠超时时间
	 */
	public static byte[] RS_autosleep_timeout=new byte[]{RS,0x02,0,0,0,0,0};
	
	/**
	 * 允许/禁止打印
	 */
	public static byte[] RS_alow_print=new byte[]{RS,0x03,0,0,0,0,0};
	
	/**
	 * 设置自动禁止打印超时时间
	 */
	public static byte[] RS_autoalow_timeout=new byte[]{RS,0x04,0,0,0,0,0};
	
	/**
	 * 查询系统电源电压
	 */
	public static byte[] RS_voltage =new byte[]{RS,0x05};
	
	/**
	 * 查询软件版本
	 */
	public static byte[] RS_version=new byte[]{RS,0x20};
	
	/**
	 * 进入串口调试模式
	 */
	public static byte[] RS_serialport_debug=new byte[]{RS,(byte) 0xde};
	
	/**
	 * 打印机复位
	 */
	public static byte[] RS_reset=new byte[]{RS,(byte) 0xdf,0x72,0x65,0x73,0x65,0x74};
	
	
	
	

	/*************************************US指令********************************************/
	
	/**
	 * 打印自检信息
	 */
	public static byte[] US_self_check=new byte[]{US,01};
	
	/**
	 * 设置QrCode二维码对齐方式
	 */
	public static byte[] US_qrcode_align=new byte[]{US,0x12,0};
	
	/**
	 * 设置QrCode二维码顶部空白高度
	 */
	public static byte[] US_qrcode_top_space=new byte[]{US,0x13,0};
	
	/**
	 * 设置QrCode二维码底部空白高度
	 */
	public static byte[] US_qrcode_bottom_space=new byte[]{US,0x14,0};
	
	/**
	 *设置QrCode二维码最小模块单元宽度 
	 */
	public static byte[] US_qrcode_element_width=new byte[]{US,0x15,0};
	
	
	

	/*************************************其他指令********************************************/
	/**
	 * 从下一制表符位置开始打印
	 */
	public static byte[] HT=new byte[]{0x09};
	
	/**
	 * 打印并换行
	 */
	public static byte[] LF=new byte[]{0x0A};
	
	/**
	 * 进纸到下一主黑标/间隙处
	 */
	public static byte[] FF=new byte[]{0x0C};
	
	/**
	 * 打印缓冲区内容
	 */
	public static byte[] CR=new byte[]{0x0D};
	
	/**
	 * 进纸到下一副黑标处
	 */
	public static byte[] SO=new byte[]{0x0E};
}
