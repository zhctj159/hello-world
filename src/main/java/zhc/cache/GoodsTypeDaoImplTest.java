package zhc.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsTypeDaoImplTest {

	private final static Logger LOGGER = LoggerFactory.getLogger(GoodsTypeDaoImplTest.class);
	
	@Autowired
	private GoodsTypeDaoImpl typeDao;

	@Test
	public void testSave() {
		String typeId = "type111";
		// 模拟第一次保存
		String returnStr1 = typeDao.save(typeId);
		LOGGER.info(returnStr1);
		// 模拟第二次保存
		String returnStr2 = typeDao.save(typeId);
		LOGGER.info(returnStr2);
	}

	@Test
	public void testUpdate() {
		String typeId = "type111";
		// 模拟第一次查询
		String returnStr1 = typeDao.select(typeId);
		LOGGER.info(returnStr1);
		// 模拟第二次查询
		String returnStr2 = typeDao.select(typeId);
		LOGGER.info(returnStr2);
		// 模拟更新
		String returnStr3 = typeDao.update(typeId);
		LOGGER.info(returnStr3);
		// 模拟查询
		String returnStr4 = typeDao.select(typeId);
		LOGGER.info(returnStr4);
	}

	@Test
	public void testDelete() {
		String typeId = "type111";
		// 模拟第一次查询
		String returnStr1 = typeDao.select(typeId);
		LOGGER.info(returnStr1);
		// 模拟第二次查询
		String returnStr2 = typeDao.select(typeId);
		LOGGER.info(returnStr2);
		// 模拟删除
		String returnStr3 = typeDao.delete(typeId);
		LOGGER.info(returnStr3);
		// 模拟查询
		String returnStr4 = typeDao.select(typeId);
		LOGGER.info(returnStr4);
	}

	@Test
	public void testSelect() {
		String typeId = "type111";
		// 模拟第一次查询
		String returnStr1 = typeDao.select(typeId);
		LOGGER.info(returnStr1);
		// 模拟第二次查询
		String returnStr2 = typeDao.select(typeId);
		LOGGER.info(returnStr2);
	}
}

/**
 * @ClassName: GoodsTypeDaoImpl
 * @Description: 模拟数据访问实现类
 * @author zhc
 */
@Repository
@CacheConfig(cacheNames = "GoodsType")
class GoodsTypeDaoImpl {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(GoodsTypeDaoImpl.class);

	@Cacheable
	public String save(String typeId) {
		LOGGER.info("save()执行了=============");
		return "模拟数据库保存";
	}

	@CachePut
	public String update(String typeId) {
		LOGGER.info("update()执行了=============");
		return "模拟数据库更新";
	}

	@CacheEvict
	public String delete(String typeId) {
		LOGGER.info("delete()执行了=============");
		return "模拟数据库删除";
	}

	@Cacheable
	public String select(String typeId) {
		LOGGER.info("select()执行了=============");
		return "模拟数据库查询";
	}
}
