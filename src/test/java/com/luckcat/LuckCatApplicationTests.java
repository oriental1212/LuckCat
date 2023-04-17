package com.luckcat;

import com.luckcat.dao.SettingMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.text.DecimalFormat;

@SpringBootTest
class LuckCatApplicationTests {

	@Resource
	SettingMapper settingMapper;
	@Test
	void contextLoads(){
		long fileS = 604440;
		System.out.println((double) 604440 / 1048576);
		DecimalFormat df = new DecimalFormat("0.00");
		String wrongSize = "0MB";
		if (fileS == 0) {
			System.out.println(wrongSize);
		}
		System.out.println(df.format((double) fileS / 1048576));
	}

}
