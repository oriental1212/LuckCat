package com.luckcat;

import com.luckcat.utils.MinioInit;
import io.minio.messages.Bucket;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;


@SpringBootTest
class LuckCatApplicationTests {
	@Resource
	MinioInit minioInit;
	@Test
	void contextLoads() throws Exception{
		List<Bucket> buckets = minioInit.createMinio().listBuckets();
		buckets.forEach((bucket) -> {
			System.out.println(bucket.name());
			System.out.println(bucket.creationDate());
			System.out.println("--------------");
		});
	}

}
