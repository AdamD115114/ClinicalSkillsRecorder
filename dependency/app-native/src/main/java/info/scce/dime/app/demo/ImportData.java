
package info.scce.dime.app.demo;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import de.ls5.dywa.generated.util.DomainFileController;
import de.ls5.dywa.generated.util.FileReference;
import de.ls5.dywa.generated.entity.dime__HYPHEN_MINUS__models.app.SkillsLog;
import de.ls5.dywa.generated.entity.dime__HYPHEN_MINUS__models.app.BaseUser;
import de.ls5.dywa.generated.entity.dime__HYPHEN_MINUS__models.app.StudentUser;

import de.ls5.dywa.generated.controller.dime__HYPHEN_MINUS__models.app.SkillsLogController;
import de.ls5.dywa.generated.controller.dime__HYPHEN_MINUS__models.app.BaseUserController;
import de.ls5.dywa.generated.controller.dime__HYPHEN_MINUS__models.app.StudentUserController;
public class ImportData{

	private final BeanManager beanManager;
	private final DomainFileController fileController;
	private final SkillsLogController skillsLogController;

	public ImportData(){
		beanManager = CDI.current().getBeanManager();
		fileController = getDomainFileController();
		skillsLogController = getSkillsLogController();
	}
	private SkillsLogController getSkillsLogController() {
		final Bean<SkillsLogController> bean = (Bean<SkillsLogController>) beanManager.resolve(beanManager.getBeans(SkillsLogController.class));
		final CreationalContext<SkillsLogController> cctx = beanManager.createCreationalContext(bean);
		final SkillsLogController skillsLogController = (SkillsLogController) beanManager.getReference(bean, bean.getBeanClass(), cctx);
		return skillsLogController;
	}
	private DomainFileController getDomainFileController() {
		final Bean<DomainFileController> bean = (Bean<DomainFileController>) beanManager.resolve(beanManager.getBeans(DomainFileController.class));
		final CreationalContext<DomainFileController> cctx = beanManager.createCreationalContext(bean);
		final DomainFileController fileController = (DomainFileController) beanManager.getReference(bean, bean.getBeanClass(), cctx);
		return fileController;
	}


	public List<SkillsLog> importData(FileReference inputFile, StudentUser newUser) {
		String newSkill = "";
		List<SkillsLog> importedSkills = new LinkedList<>();
		int counter = 0; 
		try (BufferedReader input = new BufferedReader(new InputStreamReader(fileController.loadFile(inputFile), "UTF8"))){
			newSkill = "" + input.readLine();
			while((newSkill = input.readLine()) != null) {
				parseSkillsLog(importedSkills, newSkill, newUser);
			}
		}catch(Exception e) {
			return importedSkills;
		}
		return importedSkills;
	}

	public void parseSkillsLog(List<SkillsLog> importedData, String entry, StudentUser newUser) {
		String[] data = entry.split(",");
		final SkillsLog newSkills = createSkillsLog();
		for(String i: data) {
			i.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
			i.replaceAll("\\p{C}", "");
			i.replaceAll("[^\\x00-\\x7F]", "");
			i.replaceAll("\"","");
			i.trim();
		}	
		if(data.length == 2) {
			parseCategory(newSkills, data[0]);
			parseSkill(newSkills, data[1]);
			parseAuthor(newSkills, newUser);
			setDate(newSkills);
		}else {
			System.out.println("Length:" +data.length);
			System.out.println("Error Parsing log entry.");
		}

	}
	private void parseAuthor(SkillsLog newSkill, StudentUser author) {
		newSkill.setauthor(author);
	}
	private void parseSkill(SkillsLog newSkill, String input) {
		newSkill.setskill(input);
	}
	private void parseCategory(SkillsLog newSkill, String input) {
		newSkill.setskillCategory(input);
	}
	private SkillsLog createSkillsLog() {
		return skillsLogController.create(null);
	}
	private void setDate(SkillsLog newSkill) {
		newSkill.settimeStamp(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
	}
	private static String conCat(String[] data) {
		String output = "", temp = "";
		for(int x = 1; x < data.length ; x++) {
			if(x ==1) 	output = temp;
			else		output = output + ", " +  temp;
		}
		return output;
	}
	public static List<SkillsLog> importDataSIB(FileReference inputFile, StudentUser newUser) {
		ImportData newImport = new ImportData();

		return newImport.importData(inputFile, newUser);
	}
}