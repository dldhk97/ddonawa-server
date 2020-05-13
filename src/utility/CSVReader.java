package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public class CSVReader {
	
	// 하나의 CSV 파일을 읽는 메소드
	public void read(String filePath) {
		FileInputStream input;
	    try {
	        input = new FileInputStream(new File(filePath));
	        CharsetDecoder decoder = Charset.forName("EUC-KR").newDecoder();
	        decoder.onMalformedInput(CodingErrorAction.IGNORE);
	        InputStreamReader reader = new InputStreamReader(input, decoder);
	        BufferedReader bufferedReader = new BufferedReader( reader );
	        //String line = bufferedReader.readLine();					// 헤더부분(첫줄)은 읽고 버린다
	        String line ;
	        while( (line = bufferedReader.readLine()) != null ) {
	            String[] splited = line.split(",");
	            String collect_day = splited[0];
	            String good_id = splited[1];			// good_id를 저장하면, CSV파싱할 때 같은 상품인지 알 수 있다. 그래서 저장하는게 좋을것 같다..?
	            String pum_id = splited[2];				// 품목ID는 카테고리별로 분류하기위해 필요하다
	            String good_name = splited[4];			
	            String discount_price = splited[6];		//실판매가
	            System.out.println("수집일자:" + collect_day + ", 상품ID:" + good_id + ", 품목ID:" + pum_id + ", 상품명:" + good_name + ", 가격:" + discount_price);
	            
	            // (디버깅용) 가격이 비어있는 항목 찾기
	            if(discount_price == null || discount_price.isEmpty() || discount_price == "") {
	            	bufferedReader.close();
	            	System.out.println("[가격이 비어있는 항목 발견!] " + splited.toString());
	            	return;
	            }
	        }
	        bufferedReader.close();
	        System.out.println("CSV 파싱 완료!");
	    } catch (FileNotFoundException e) {
	        IOHandler.getInstance().log(e.getMessage());
	    } catch( IOException e ) {
	    	IOHandler.getInstance().log(e.getMessage());
	    }
	}
}
