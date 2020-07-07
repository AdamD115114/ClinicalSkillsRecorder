package info.scce.dime.app.demo;

import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import de.ls5.dywa.generated.entity.dime__HYPHEN_MINUS__models.app.SkillsList;
import de.ls5.dywa.generated.controller.dime__HYPHEN_MINUS__models.app.SkillsListController;
import de.ls5.dywa.generated.entity.dime__HYPHEN_MINUS__models.app.Category;
import de.ls5.dywa.generated.controller.dime__HYPHEN_MINUS__models.app.CategoryController;
public class SkillsLoader{

	private final BeanManager beanManager;
	private final SkillsListController skillsListController;
	private final CategoryController categoryController;

	public SkillsLoader() {
		beanManager = CDI.current().getBeanManager();
		skillsListController = getSkillsListController();
		categoryController = GetCategoryController();
	}
	private SkillsListController getSkillsListController() {
		final Bean<SkillsListController> bean = (Bean<SkillsListController>) beanManager.resolve(beanManager.getBeans(SkillsListController.class));
		final CreationalContext<SkillsListController> cctx = beanManager.createCreationalContext(bean);
		final SkillsListController skillsListController = (SkillsListController) beanManager.getReference(bean, bean.getBeanClass(), cctx);
		return skillsListController;
	}
	private CategoryController GetCategoryController() {
		final Bean<CategoryController> bean = (Bean<CategoryController>) beanManager.resolve(beanManager.getBeans(CategoryController.class));
		final CreationalContext<CategoryController> cctx = beanManager.createCreationalContext(bean);
		final CategoryController categoryController = (CategoryController) beanManager.getReference(bean, bean.getBeanClass(), cctx);
		return categoryController;
	}

	private List<SkillsList>  populate(){
		List<SkillsList> output = new LinkedList<>();
		InputStream stream = null;
		try {
			URL content = new URL("http://fyphostserver.ddns.net/SkillsList/SkillsMatrix.csv");
			stream = content.openStream();
		}catch (Exception e) {
			e.printStackTrace();
		}
		String row = "";
		int count = 0;
		String cat = "",temp = "", breaker = "break";
		boolean checker = true;
		try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(stream,  StandardCharsets.UTF_8))){
			while ((row = csvReader.readLine()) != null) {
				checker = true;
				final SkillsList eachList = getSkillsList();
				List<String> skills = new LinkedList<>();
				while(checker && (row = csvReader.readLine()) != null) {
					String Skill = "";
					String[] data = row.split(",");
					if(data[0].equals(breaker)) {
						break;
					}
					for(String i: data) {
						i.replaceAll("\\p{Cntrl}", "");
					}
					cat = data[0];
					if(data.length > 2) {
						Skill = conCat(data);
					}else {
						Skill = data[1];
						Skill = Skill.replaceAll("[^\\a-zA-Z_0-9()\\[\\]]", " ");
					}
					if(!temp.equals(cat)) {
						eachList.setcategory(pickCategory(cat));
					}
					skills.add(Skill);
					temp = cat;
				}
				eachList.setskills(skills);
				output.add(eachList);
			}
			csvReader.close();
		}catch(Exception e) {
			e.printStackTrace();
	}
	return output;
}
public static List<SkillsList> LoadSkills(){
	SkillsLoader loader = new SkillsLoader();
	return loader.populate();
}
private String conCat(String[] data) {
	String output = "", temp = "";
	for(int x = 1; x < data.length ; x++) {
		temp = data[x].replaceAll("[^\\a-zA-Z_0-9()\\[\\]]", " ");
		if(x ==1) 	output = temp;
		else		output = output + ", " +  temp;
	}
	return output;
}
private Category pickCategory(String input) {
	Category output = Category.MEDICATION;
	switch(input) {
	case "MEDICATION":
		output = Category.MEDICATION;
		break; 
	case "AIRWAY & BREATHING MANAGEMENT":
		output = Category.AIRWAY___AMPERSAND___BREATHING_MANAGEMENT;
		break;
	case "CARDIAC":
		output = Category.CARDIAC;
		break;
	case "HAEMORRHAGE CONTROL":
		output = Category.HAEMORRHAGE_CONTROL;
		break;
	case "MEDICATION ADMINISTRATION":
		output = Category.MEDICATION_ADMINISTRATION;
		break;
	case "TRAUMA":
		output = Category.TRAUMA;
		break;
	case "OTHER":
		output = Category.OTHER;
		break;
	case "PATIENT ASSESSMENT":
		output = Category.PATIENT_ASSESSMENT;
		break;
	default: 
		System.out.println("Method: HIT DEFAULT CASE input: " + input);
	}
	return output;
}
private SkillsList getSkillsList() {
	return skillsListController.create(null);
}
public static String convertCategoryToText(Category input) {
	String out = "";
	switch(input) {
	case MEDICATION:
		out = "MEDICATION";
		break; 
	case AIRWAY___AMPERSAND___BREATHING_MANAGEMENT:
		out =  "AIRWAY & BREATHING MANAGEMENT";
		break;	
	case CARDIAC:
		out = "CARDIAC";
		break;
	case HAEMORRHAGE_CONTROL:
		out = "HAEMORRHAGE CONTROL";
		break;
	case MEDICATION_ADMINISTRATION:
		out = "MEDICATION ADMINISTRATION";
		break;
	case TRAUMA:
		out = "TRAUMA";
		break;
	case OTHER:
		out = "OTHER";
		break;
	case PATIENT_ASSESSMENT:
		out = "PATIENT ASSESSMENT";
		break;
	default: 
		out =  "No Matching Category.";
	}
	return out;
}
}