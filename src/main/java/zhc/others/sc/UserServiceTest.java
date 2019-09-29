package zhc.others.sc;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceTest extends SpringTestCase {
	@Autowired
	private User1Service user1Service;
	@Autowired
	private User2Service user2Service;

	@Test
	@Transactional(rollbackFor=Exception.class)
	public void test71() {
		User user = new User();
		user.setId(711);
		user.setName("s711");
		user1Service.addNever(user);

//		throw new RuntimeException();
	}
	
	@Test
	@Transactional(rollbackFor=Exception.class)
	public void test61() {
		User user = new User();
		user.setId(611);
		user.setName("s611");
		try {
			user1Service.addNotSupportedException(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		user.setId(612);
		user.setName("s612");
		user1Service.addNotSupported(user);
		
		throw new RuntimeException();
	}
	
	@Test
	@Transactional(rollbackFor=Exception.class)
	public void test51() {
		User user = new User();
		user.setId(511);
		user.setName("s511");
		try {
			user1Service.addSupportsException(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		user.setId(512);
		user.setName("s512");
		user1Service.addSupports(user);
		
		throw new RuntimeException();
	}
	
	@Test
	@Transactional(rollbackFor=Exception.class)
	public void test41() {
		User user = new User();
		user.setId(411);
		user.setName("s411");
		user1Service.addMandatory(user);

		throw new RuntimeException();
	}

	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void test33() {
		User user = new User();
		user.setId(331);
		user.setName("s331");
		user1Service.addNested(user);
		user.setId(332);
		user.setName("s332");
		user2Service.addNested(user);
		throw new RuntimeException();
	}

	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void test34() {
		User user = new User();
		user.setId(341);
		user.setName("s341");
		user1Service.addNested(user);
		user.setId(342);
		user.setName("s342");
		user2Service.addNestedException(user);
	}

	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void test35() {
		User user = new User();
		user.setId(351);
		user.setName("s351");
		user1Service.addNested(user);
		user.setId(352);
		user.setName("s352");
		try {
			user2Service.addNestedException(user);
		} catch (Exception e) {
			System.out.println("rollback");
		}
	}

	@Test
	public void test31() {
		User user = new User();
		user.setId(311);
		user.setName("s311");
		user1Service.addNested(user);
		user.setId(312);
		user.setName("s312");
		user2Service.addNested(user);
		throw new RuntimeException();
	}

	@Test
	public void test32() {
		User user = new User();
		user.setId(321);
		user.setName("s321");
		user1Service.addNested(user);
		user.setId(322);
		user.setName("s322");
		user2Service.addNestedException(user);
	}

	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void test23() {
		User user = new User();
		user.setId(231);
		user.setName("s231");
		user1Service.addRequired(user);
		user.setId(232);
		user.setName("s232");
		user2Service.addRequiresNew(user);
		user.setId(233);
		user.setName("s233");
		user2Service.addRequiresNew(user);
		throw new RuntimeException();
	}

	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void test24() {
		User user = new User();
		user.setId(241);
		user.setName("s241");
		user1Service.addRequired(user);
		user.setId(242);
		user.setName("s242");
		user2Service.addRequiresNew(user);
		user.setId(243);
		user.setName("s243");
		user2Service.addRequiresNewException(user);
	}

	@Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
	public void test25() {
		User user = new User();
		user.setId(251);
		user.setName("s251");
		user1Service.addRequired(user);
		user.setId(252);
		user.setName("s252");
		user2Service.addRequiresNew(user);
		user.setId(253);
		user.setName("s253");
		try {
			user2Service.addRequiresNewException(user);
		} catch (Exception e) {
			System.out.println("rollback");
		}
	}

	@Test
	public void test21() {
		User user = new User();
		user.setId(211);
		user.setName("s211");
		user1Service.addRequiresNew(user);
		user.setId(212);
		user.setName("s212");
		user2Service.addRequiresNew(user);
		throw new RuntimeException();
	}

	@Test
	public void test22() {
		User user = new User();
		user.setId(221);
		user.setName("s221");
		user1Service.addRequiresNew(user);
		user.setId(21);
		user.setName("s222");
		user2Service.addRequiresNewException(user);
	}

	@Test
	public void test11() {
		User user = new User();
		user.setId(111);
		user.setName("s111");
		user1Service.addRequired(user);
		user.setId(21);
		user.setName("s112");
		user2Service.addRequired(user);
		throw new RuntimeException();
	}

	@Test
	public void test12() {
		User user = new User();
		user.setId(121);
		user.setName("s121");
		user1Service.addRequired(user);
		user.setId(122);
		user.setName("s122");
		user2Service.addRequiredException(user);
	}

}
