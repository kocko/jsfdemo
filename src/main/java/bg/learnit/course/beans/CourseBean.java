package bg.learnit.course.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import bg.learnit.course.db.model.Course;
import bg.learnit.course.service.CourseService;

@SessionScoped
@ManagedBean(name = "courseBean")
public class CourseBean {
	
	@ManagedProperty(name = "courseService", value = "#{courseService}")
	private CourseService courseService;
	
	private Part picture;
	
	private String name;
	
	private Date startDate;
	
	private Date endDate;
	
	private String tags;
	
	public CourseService getCourseService() {
		return courseService;
	}

	public void setCourseService(CourseService courseService) {
		this.courseService = courseService;
	}

	public Part getPicture() {
		return picture;
	}

	public void setPicture(Part picture) {
		this.picture = picture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public List<Course> getCourses() {
		return courseService.getAllCourses();
	}
	
	private ByteArrayInputStream pictureStream;
	
	public StreamedContent getCoursePicture() {
		FacesContext context = FacesContext.getCurrentInstance();
		
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
        	return new DefaultStreamedContent();
        } else {
        	if (picture == null) {
	    		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
	    		String name = params.get("courseName");
	        	Course course = courseService.findCourse(name);
	        	System.out.println("Course is null? " + (course == null));
	        	pictureStream = new ByteArrayInputStream(course.getPicture());
	        	return new DefaultStreamedContent(pictureStream);
        	}
        	return new DefaultStreamedContent();
        }
	}

	public String save() throws IOException {
		Set<String> tagSet = new HashSet<String>();
		if (tags != null) {
			String[] split = tags.split(",");
			for (String item : split) {
				tagSet.add(item.trim());
			}
		}
		
		byte[] pictureAsBytes = null;
		if (picture != null) {
			pictureAsBytes = IOUtils.toByteArray(picture.getInputStream());
		}
		courseService.saveCourse(name, startDate, endDate, tagSet, pictureAsBytes);
		return "/pages/home/courses";
	}
}
