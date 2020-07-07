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
/**
 * Collection of static methods for native SIBs
 */
public class Native {


	private static DomainFileController getDomainFileController() {
		final BeanManager bm = CDI.current().getBeanManager();
		final Bean<DomainFileController> bean = (Bean<DomainFileController>) bm.resolve(bm.getBeans(DomainFileController.class));
		final CreationalContext<DomainFileController> cctx = bm.createCreationalContext(bean);
		final DomainFileController fileController = (DomainFileController) bm.getReference(bean, bean.getBeanClass(), cctx);
		return fileController;
	}
	private static String conCat(String[] data) {
		String output = "", temp = "";
		for(int x = 1; x < data.length ; x++) {
			temp = data[x].replaceAll("[^\\a-zA-Z_0-9()\\[\\]]", " ");
			if(x ==1) 	output = temp;
			else		output = output + ", " +  temp;
		}
		return output;
	}

	public static String genUUID() {
		UUID uuid = UUID.randomUUID();
		return ""+uuid.toString();
	}

	public static String exportToText(List<SkillsLog> input) {
		char LINE_BREAK = '\n';
		String header = "Skill Category, Skill, Author, Date\n";
		StringBuffer buffer = new StringBuffer();
		buffer.append(header);
		for (SkillsLog i : input) {
			buffer.append(asString(i));
			buffer.append(LINE_BREAK);
		}
		return buffer.toString();
	}
	static String asString(SkillsLog input) {

		if (input == null || "".equals(input)) {
			return "";
		}
		String DELIMITER = ",";
		StringBuffer buffer = new StringBuffer();
		try {
			buffer.append(csvEscape(input.getskillCategory()));
			buffer.append(DELIMITER);	
		} catch (Exception e) {
			System.out.println("Method: asString -  Issue Appending skillsCategory" );
		}
		try {
			buffer.append(csvEscape(input.getskill()));
			buffer.append(DELIMITER);
		} catch (Exception e) {
			System.out.println("Method: asString -  Issue Appending skill" );
		}
		try {
			try {
				StudentUser temp = input.getauthor();
				BaseUser temp1 = temp.getbaseUser();
				buffer.append(csvEscape(temp1.getusername()));
				buffer.append(DELIMITER);	
			}catch(Exception e) {
				System.out.println("Error While attempting to reference input.author.baseUser.getusername()");
			}
		} catch (Exception e) {
			System.out.println("Method: asString -  Issue Appending baseUser.getusername()");
		}try {
			buffer.append(csvEscape(input.gettimeStamp()));
		}catch(Exception e) {
			System.out.println("Method: asString -  Issue Appending TimeStamp");
		}

		return buffer.toString();
	}
	private static String csvEscape(Object input) {
		if (input == null) {
			return "";
		}
		String output = String.valueOf(input).trim();
		output = output.replaceAll("\"", "\\\"");
		output = "\"" + output + "\"";
		return output;
	}
	public static FileReference exportToFile(List<SkillsLog> skillsLog, boolean flag) {
		String fileName = "";
		if(flag) {
			fileName = fileName + "TotalSkillsReport";
		}else {
			StudentUser temp = skillsLog.get(0).getauthor();
			BaseUser temp1 = temp.getbaseUser();
			fileName = fileName + "" + temp1.getusername();
		}
		String fileSuffix = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
		InputStream is = new ByteArrayInputStream(exportToText(skillsLog).getBytes(StandardCharsets.UTF_8));
		return getDomainFileController().storeFile(fileName + "" + fileSuffix +".csv", is);
	}

	public static String getDate() {
		return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	}
	public static boolean isAuthorNull(SkillsLog input) {
		if(input.getauthor() == null){
			return true;
		}else {
			return false;
		}
	}

}

