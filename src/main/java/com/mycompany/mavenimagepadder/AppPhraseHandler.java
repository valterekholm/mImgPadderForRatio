package com.mycompany.mavenimagepadder;

import java.io.Console;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AppPhraseHandler {
	AppLanguage appLanguage;//setting for app - "my language" TODO: move to settings
	static AppLanguage englishLanguage;//for reference
	ArrayList<AppPhrase> words = new ArrayList<>();
	
	ArrayList<AppLanguage> appLanguages = new ArrayList<AppLanguage>();//all used in app, first must be english
	
	public AppPhraseHandler(AppLanguage applanguage) {
		//AppLanguage engL = ; 
		appLanguage = applanguage; //"my language"
		englishLanguage = new AppLanguage("Enlish", "en");//just like that
		appLanguages.add(englishLanguage);
		appLanguages.add(appLanguage);
	}
	
	public String[] getLanguageItems() {
		String[] li = new String[appLanguages.size()];
		
		for(int i=0; i<li.length; i++) {
			li[i] = appLanguages.get(i).getName();
		}
		
		return li;
	}

	public AppPhrase addWord(AppPhrase appPhrase) {
		
		words.add(appPhrase);
		
		return appPhrase;
	}
	
	public AppPhrase addEnglishWord(String word) {
		
		AppPhrase appPhrase = new AppPhrase(englishLanguage, word, null);
		
		words.add(appPhrase);
		
		return appPhrase;
	}
	
	public Boolean addPhrasesInMyLanguage(AppLanguage myLanguage, String[] englishWords, String[] myWords) {
		if(englishWords.length != myWords.length) {
			return false;
		}
		int length = englishWords.length;
		for(int i=0; i<length; i++) {
			AppPhrase engPh = addEnglishWord(englishWords[i]);
			addWord(new AppPhrase(myLanguage, myWords[i], engPh));
		}
		return true;
	}
	
	/**
	 * 
	 * @param languages array of the types of languages
	 * @param wordings - first one must be in english, it can be 1 or more in total
	 * @return
	 */
	public Boolean addPhrasesInLanguages(AppLanguage[] languages, String[] ...wordings) {
		
		int lenArgs = wordings.length;//number of languages
		
		if(lenArgs == 0 || languages.length == 0) {
			return false;
		}
		
		if(lenArgs>1) {
			int lastLength = wordings[0].length;
			
			for(int i=1; i<lenArgs; i++) {
				//compare length, every phrase translated?
				if(lastLength != wordings[i].length) {
					return false;
				}
				lastLength = wordings[i].length;
			}
		}
		
		if(languages.length != lenArgs) {
			//languages declared not same amount as sent lists
			return false;
		}
		
		if(languages[0] != AppPhraseHandler.englishLanguage){
			System.out.println("First language supplied must be english...");
			return false;
		}
		
		int nrOfPhrases = wordings[0].length;
		
		for(int i=0; i<nrOfPhrases; i++) {
			AppPhrase engPh = addEnglishWord(wordings[0][i]);
			if(lenArgs>1) {
				//more languages
				for(int j=1; j<lenArgs; j++) {
					addWord(new AppPhrase(appLanguages.get(j), wordings[j][i], engPh));
				}
			}
		}
		return true;
	}
	
	public AppPhrase getPhraseInMyLanguageFromEnlishRefSearch(String englishSearch) {
		//englishSearch = englishSearch.toLowerCase();
		ArrayList<AppPhrase> foundEnglishWords =
		(ArrayList<AppPhrase>) words
		.stream()
		.filter(p -> p.get_language() == AppPhraseHandler.englishLanguage
		&& p.get_phrase().toLowerCase()
		.contains(englishSearch.toLowerCase())
		)
		.collect(Collectors.toList()
				);//TODO: improve word search
		
		//Pattern.compile(Pattern.quote(englishSearch), Pattern.CASE_INSENSITIVE).matcher("Hej").find();
		
		int choosenIndex;
		
		if(foundEnglishWords.size() > 1) {
			System.out.println("Your search with '"+englishSearch+"' had more than one match;\n");
			foundEnglishWords.stream().forEach(p -> System.out.println(p));
			
			//take the most similar in length...
			int count = 0;
			int diffLength = Integer.MAX_VALUE;
			int minDiffIndex = 0;
			for(AppPhrase p : foundEnglishWords) {
				int diff = Math.abs(p.get_phrase().length() - englishSearch.length());
				if(diff < diffLength) {
					diffLength = diff;
					minDiffIndex = count;
				}
				count++;
			}
			choosenIndex = minDiffIndex;
			System.out.println("Choosing index " + choosenIndex);
		}
		else if (foundEnglishWords.size() == 1){
			choosenIndex = 0;
		}
		else {
			System.out.println("Your search with '" + englishSearch + "' had no match...");
			choosenIndex = -1;
			return new AppPhrase(englishLanguage, "-", null);
		}
		
		
		
		System.out.println("All words");
		for(AppPhrase p : words) {
			System.out.println(p);
		}
		
		//Takes the phrases in your language
		ArrayList<AppPhrase> foundMyWords = (ArrayList<AppPhrase>) words.parallelStream().filter(p -> p.get_referenceEnglishPhrase() == foundEnglishWords.get(choosenIndex)).collect(Collectors.toList());
		
		return foundMyWords.get(0);
	}
}
