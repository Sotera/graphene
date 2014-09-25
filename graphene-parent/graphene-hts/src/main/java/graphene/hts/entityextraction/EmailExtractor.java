package graphene.hts.entityextraction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailExtractor {
	private final String RE_MAIL = "([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})";

	public Collection<String> extractEmails(String source) {
		Pattern p = Pattern.compile(RE_MAIL);
		Matcher m = p.matcher(source);
		Set<String> emailList = new HashSet<String>();
		while (m.find()) {
			emailList.add(m.group(1));
		}
		return emailList;
	}
}
