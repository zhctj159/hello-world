package zhc.others.sc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class User1Service {

	@Autowired(required=false)
	private User1Mapper mapper;
	
	/** REQUIRED：总是运行在事务中。如果当前存在事务，则加入当前事务，如果不存在则新建事务运行 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void addRequired(User user) {
		mapper.addUser(user);
	}
	
	/** REQUIRES_NEW：总是新建一个事务执行，如果当前存在事务，则挂起当前事务 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void addRequiresNew(User user) {
		mapper.addUser(user);
	}
	
	/** NESTED： 作为当前事务的嵌套事务，可单独提交、回滚 */
	@Transactional(propagation = Propagation.NESTED)
    public void addNested(User user){
        mapper.addUser(user);
    }
	
	/** MANDATORY：必须运行在事务中，如当前不存在事务，则抛出异常 */
	@Transactional(propagation = Propagation.MANDATORY)
    public void addMandatory(User user){
        mapper.addUser(user);
    }
	
	/** SUPPORTES：不需要事务上下文，如有则加入事务，如无正常运行 */
	@Transactional(propagation = Propagation.SUPPORTS)
    public void addSupports(User user){
        mapper.addUser(user);
    }
	
	/** SUPPORTES：不需要事务上下文，如有则加入事务，如无正常运行 */
	@Transactional(propagation = Propagation.SUPPORTS)
    public void addSupportsException(User user){
        mapper.addUser(user);
        throw new RuntimeException();
    }
	
	/** NOT_SUPPORTED：不允许运行在事务中，如有事务则挂起该事务 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addNotSupported(User user){
        mapper.addUser(user);
    }
	
	/** NOT_SUPPORTED：不允许运行在事务中，如有事务则挂起该事务 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addNotSupportedException(User user){
        mapper.addUser(user);
        throw new RuntimeException();
    }
	
	/** NEVER：不允许运行在事务中，如果当前有事务则报错 */
	@Transactional(propagation = Propagation.NEVER)
    public void addNever(User user){
        mapper.addUser(user);
    }
}
