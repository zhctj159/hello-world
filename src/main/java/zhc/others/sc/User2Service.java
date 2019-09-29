package zhc.others.sc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class User2Service {
	
	@Autowired(required=false)
	private User2Mapper mapper;

	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	public void addRequired(User user) {
		mapper.addUser(user);
	}
	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	public void addRequiredException(User user) {
		mapper.addUser(user);
		throw new RuntimeException();
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW,rollbackFor=Exception.class)
	public void addRequiresNew(User user) {
		mapper.addUser(user);
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW,rollbackFor=Exception.class)
	public void addRequiresNewException(User user) {
		mapper.addUser(user);
		throw new RuntimeException();
	}
	
	@Transactional(propagation = Propagation.NESTED,rollbackFor=Exception.class)
    public void addNested(User user){
		mapper.addUser(user);
    }
	
	@Transactional(propagation = Propagation.NESTED,rollbackFor=Exception.class)
    public void addNestedException(User user){
		mapper.addUser(user);
    }
}
