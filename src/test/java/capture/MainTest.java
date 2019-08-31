package capture;

import java.util.Date;

import com.ly.util.DateUtil;
import com.ly.util.PropertieUtil;

public class MainTest {

	
	public static void main(String[] args) {
		String excelFilePath = PropertieUtil.getValue("excelFilePath") + DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYMMDD)
		+ PropertieUtil.getValue("excelFilePostfix");
		
		System.out.println(excelFilePath);
	}
}
