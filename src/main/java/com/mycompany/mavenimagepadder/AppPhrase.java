package com.mycompany.mavenimagepadder;

public class AppPhrase {
	private AppLanguage _language;
	private String _phrase;
	private AppPhrase _referenceEnglishPhrase;
	
	public AppPhrase(AppLanguage language, String phrase, AppPhrase rerefenceEnglishPhrase) {
		_language = language;
		_phrase = phrase;
		if(_referenceEnglishPhrase != null) {
			//non-english
			System.out.println("New non-english phrase");
		}
		else {
			System.out.println("New english phrase");
			if(language == null) {
				_language = AppPhraseHandler.englishLanguage;
			}
			else {
				if(! language.equals(AppPhraseHandler.englishLanguage)) {
					System.out.println("You supplied a phrase without a referencing english phrase, meaning an english phrase, but the language didn't equal the AppPhraseHandler.englishLanguage");
				}
			}
		}
		_referenceEnglishPhrase = rerefenceEnglishPhrase;
	}

	public AppLanguage get_language() {
		return _language;
	}

	public void set_language(AppLanguage _language) {
		this._language = _language;
	}

	public String get_phrase() {
		return _phrase;
	}

	public void set_phrase(String _phrase) {
		this._phrase = _phrase;
	}

	public AppPhrase get_referenceEnglishPhrase() {
		return _referenceEnglishPhrase;
	}

	public void set_referenceEnglishPhrase(AppPhrase _referenceEnglishPhrase) {
		this._referenceEnglishPhrase = _referenceEnglishPhrase;
	}

	@Override
	public String toString() {
		return "AppPhrase [_language=" + _language + ", _phrase=" + _phrase + ", _referenceEnglishPhrase="
				+ _referenceEnglishPhrase + "]";
	}
	
	

}
