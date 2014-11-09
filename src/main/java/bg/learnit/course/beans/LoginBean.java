package bg.learnit.course.beans;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import bg.learnit.course.db.model.Course;
import bg.learnit.course.db.model.User;
import bg.learnit.course.service.UsersService;

/**
 * 
 * @author kocko
 *
 */

@SessionScoped
@ManagedBean(name = "loginBean")
public class LoginBean {

	private String email;

	private String password;
	
	private User loggedInUser;
	
	private Course selectedCourse;
	
	@ManagedProperty(name = "usersService", value = "#{usersService}")
	private UsersService usersService;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public User getLoggedInUser() {
		return loggedInUser;
	}
	
	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
	
	public Course getSelectedCourse() {
		return selectedCourse;
	}
	
	public void setSelectedCourse(Course selectedCourse) {
		this.selectedCourse = selectedCourse;
	}
	
	public UsersService getUsersService() {
		return usersService;
	}
	
	public void setUsersService(UsersService usersService) {
		this.usersService = usersService;
	}
	
	public boolean isLoggedIn() {
		return loggedInUser != null;
	}
	
	public String login() {
		loggedInUser = usersService.findUser(email, password);
		if (loggedInUser == null) {
			ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("login.error"), bundle.getString("login.invalidcredentials"));
			FacesContext.getCurrentInstance().addMessage(null, message);
			return null;
		}
		password = null;
		return "/pages/home/index.jsf?faces-redirect=true";
	}
	
	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();;
		return "/pages/login.jsf?faces-redirect=true";
	}
	
	public String enrollToCourse(String courseName) {
		boolean success = loggedInUser.getCourses().add(courseName);
		if (!success) {
			//Most probably, the course already exists in the Set.
			//Although the user will be disallowed to click the UI button, a safety check should be applied.
			//TODO: Add a FacesMessage with an error, saying that the selected course already exists in the Set.
			return null;
		}
		usersService.updateUser(loggedInUser);
		return "/pages/home/index";
	}


}
