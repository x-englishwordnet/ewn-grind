package org.ewn.grind;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maps information into a documented numerical code
 *
 * @author Bernard Bou
 */
public class Coder
{
	private Coder()
	{
	}

	// R E L A T I O N

	private static final String ANTONYM = "antonym";

	private static final String HYPERNYM = "hypernym";

	private static final String INSTANCE_HYPERNYM = "instance_hypernym";

	private static final String HYPONYM = "hyponym";

	private static final String INSTANCE_HYPONYM = "instance_hyponym";

	private static final String HOLO_MEMBER = "holo_member";

	private static final String HOLO_SUBSTANCE = "holo_substance";

	private static final String HOLO_PART = "holo_part";

	private static final String MERO_MEMBER = "mero_member";

	private static final String MERO_SUBSTANCE = "mero_substance";

	private static final String MERO_PART = "mero_part";

	private static final String ATTRIBUTE = "attribute";

	private static final String PERTAINYM = "pertainym";

	private static final String DERIVATION = "derivation";

	private static final String DOMAIN_TOPIC = "domain_topic";

	private static final String HAS_DOMAIN_TOPIC = "has_domain_topic";

	private static final String DOMAIN_REGION = "domain_region";

	private static final String HAS_DOMAIN_REGION = "has_domain_region";

	private static final String DOMAIN_USAGE = "exemplifies";

	private static final String HAS_DOMAIN_USAGE = "is_exemplified_by";

	private static final String ALSO = "also";

	private static final String ENTAILS = "entails";

	private static final String SIMILAR = "similar";

	private static final String VERB_GROUP = "similar";

	private static final String PARTICIPLE = "participle";

	private static final String CAUSES = "causes";

	/**
	 * Code relation
	 *
	 * @param type relation type
	 * @param pos  part-of-speech
	 * @return code
	 */
	static String codeRelation(String type, char pos)
	{
		switch (pos)
		{
			case 'n':
			/* 
			@formatter:off
			!    Antonym 
			@    Hypernym 
			@i    Instance Hypernym 
			 ~    Hyponym 
			 ~i    Instance Hyponym 
			#m    Member holonym 
			#s    Substance holonym 
			#p    Part holonym 
			%m    Member meronym 
			%s    Substance meronym 
			%p    Part meronym 
			=    Attribute 
			+    Derivationally related form         
			;c    Domain of synset - TOPIC 
			-c    Member of this domain - TOPIC 
			;r    Domain of synset - REGION 
			-r    Member of this domain - REGION 
			;u    Domain of synset - USAGE 
			-u    Member of this domain - USAGE
			@formatter:on
			*/
				switch (type)
				//@formatter:off
				{
					case ANTONYM:
						return "!";
					case HYPERNYM:
						return "@";
					case INSTANCE_HYPERNYM:
						return "@i";
					case HYPONYM:
						return "~";
					case INSTANCE_HYPONYM:
						return "~i";
					case HOLO_MEMBER:
						return "#m";
					case HOLO_SUBSTANCE:
						return "#s";
					case HOLO_PART:
						return "#p";
					case MERO_MEMBER:
						return "%m";
					case MERO_SUBSTANCE:
						return "%s";
					case MERO_PART:
						return "%p";
					case ATTRIBUTE:
						return "=";
					case PERTAINYM:
						return "\\"; //NS
					case DERIVATION:
						return "+";
					case DOMAIN_TOPIC:
						return ";c";
					case HAS_DOMAIN_TOPIC:
						return "-c";
					case DOMAIN_REGION:
						return ";r";
					case HAS_DOMAIN_REGION:
						return "-r";
					case DOMAIN_USAGE:
						return ";u";
					case HAS_DOMAIN_USAGE:
						return "-u";
					default:
						break;
				}
				//@formatter:on
				break;

			case 'v':
			/*
			@formatter:off
			!    Antonym 
			@    Hypernym 
			 ~    Hyponym 
			*    Entailment 
			>    Cause 
			^    Also see 
			$    Verb Group 
			+    Derivationally related form         
			;c    Domain of synset - TOPIC 
			;r    Domain of synset - REGION 
			;u    Domain of synset - USAGE 
			@formatter:on
			*/
				switch (type)
				//@formatter:off
				{
					case ANTONYM:
						return "!";
					case HYPERNYM:
						return "@";
					case HYPONYM:
						return "~";
					case ENTAILS:
						return "*";
					case CAUSES:
						return ">";
					case ALSO:
						return "^";
					case VERB_GROUP:
						return "$"; // verb group
					case DERIVATION:
						return "+";
					case DOMAIN_TOPIC:
						return ";c";
					case DOMAIN_REGION:
						return ";r";
					case DOMAIN_USAGE:
						return ";u";
					default:
						break;
				}
				//@formatter:on
				break;

			case 'a':
			case 's':
			/*
			@formatter:off
			!    Antonym 
			&    Similar to 
			<    Participle of verb 
			\    Pertainym (pertains to noun) 
			=    Attribute 
			^    Also see 
			;c    Domain of synset - TOPIC 
			;r    Domain of synset - REGION 
			;u    Domain of synset - USAGE
			@formatter:on			
			*/
				switch (type)
				//@formatter:off
				{
					case ANTONYM:
						return "!";
					case SIMILAR:
						return "&";
					case PARTICIPLE:
						return "<";
					case PERTAINYM:
						return "\\";
					case ATTRIBUTE:
						return "=";
					case ALSO:
						return "^";
					case DERIVATION:
						return "+"; //NS

					case DOMAIN_TOPIC:
						return ";c";
					case DOMAIN_REGION:
						return ";r";
					case DOMAIN_USAGE:
						return ";u";

					case HAS_DOMAIN_TOPIC:
						return "-c"; //NS
					case HAS_DOMAIN_REGION:
						return "-r"; //NS
					case HAS_DOMAIN_USAGE:
						return "-u"; //NS
					default:
						break;
				}
				//@formatter:on
				break;

			case 'r':
			/*
			 @formatter:off
			 !    Antonym 
			 \    Derived from adjective 
			 ;c    Domain of synset - TOPIC 
			 ;r    Domain of synset - REGION 
			 ;u    Domain of synset - USAGE 
			 @formatter:on
			 */
				switch (type)
				//@formatter:off
				{
					case ANTONYM:
						return "!";
					case PERTAINYM:
						return "\\"; // NS
					case DERIVATION:
						return "+";

					case DOMAIN_TOPIC:
						return ";c";
					case DOMAIN_REGION:
						return ";r";
					case DOMAIN_USAGE:
						return ";u";

					case HAS_DOMAIN_TOPIC:
						return "-c"; //NS
					case HAS_DOMAIN_REGION:
						return "-r"; //NS
					case HAS_DOMAIN_USAGE:
						return "-u"; //NS
					default:
						break;
				}
				//@formatter:on
				break;

			default:
				break;
		}
		throw new IllegalArgumentException("pos=" + pos + " relType=" + type);
	}

	// V E R B F R A M E

	private static final Map<String, Integer> FRAME_TO_NUM = new HashMap<>();

	static
	{
		FRAME_TO_NUM.put("Something ----s", 1);
		FRAME_TO_NUM.put("Somebody ----s", 2);
		FRAME_TO_NUM.put("It is ----ing", 3);
		FRAME_TO_NUM.put("Something is ----ing PP", 4);
		FRAME_TO_NUM.put("Something ----s something Adjective/Noun", 5);
		FRAME_TO_NUM.put("Something ----s Adjective/Noun", 6);
		FRAME_TO_NUM.put("Somebody ----s Adjective", 7);
		FRAME_TO_NUM.put("Somebody ----s something", 8);
		FRAME_TO_NUM.put("Somebody ----s somebody", 9);
		FRAME_TO_NUM.put("Something ----s somebody", 10);
		FRAME_TO_NUM.put("Something ----s something", 11);
		FRAME_TO_NUM.put("Something ----s to somebody", 12);
		FRAME_TO_NUM.put("Somebody ----s on something", 13);
		FRAME_TO_NUM.put("Somebody ----s somebody something", 14);
		FRAME_TO_NUM.put("Somebody ----s something to somebody", 15);
		FRAME_TO_NUM.put("Somebody ----s something from somebody", 16);
		FRAME_TO_NUM.put("Somebody ----s somebody with something", 17);
		FRAME_TO_NUM.put("Somebody ----s somebody of something", 18);
		FRAME_TO_NUM.put("Somebody ----s something on somebody", 19);
		FRAME_TO_NUM.put("Somebody ----s somebody PP", 20);
		FRAME_TO_NUM.put("Somebody ----s something PP", 21);
		FRAME_TO_NUM.put("Somebody ----s PP", 22);
		FRAME_TO_NUM.put("Somebody's (body part) ----s", 23);
		FRAME_TO_NUM.put("Somebody ----s somebody to INFINITIVE", 24);
		FRAME_TO_NUM.put("Somebody ----s somebody INFINITIVE", 25);
		FRAME_TO_NUM.put("Somebody ----s that CLAUSE", 26);
		FRAME_TO_NUM.put("Somebody ----s to somebody", 27);
		FRAME_TO_NUM.put("Somebody ----s to INFINITIVE", 28);
		FRAME_TO_NUM.put("Somebody ----s whether INFINITIVE", 29);
		FRAME_TO_NUM.put("Somebody ----s somebody into V-ing something", 30);
		FRAME_TO_NUM.put("Somebody ----s something with something", 31);
		FRAME_TO_NUM.put("Somebody ----s INFINITIVE", 32);
		FRAME_TO_NUM.put("Somebody ----s VERB-ing", 33);
		FRAME_TO_NUM.put("It ----s that CLAUSE", 24);
		FRAME_TO_NUM.put("Something ----s INFINITIVE", 25);
	}

	/**
	 * Code verb frame
	 *
	 * @param frame frame text
	 * @return code
	 */
	static int codeFrame(String frame)
	{
		return FRAME_TO_NUM.get(frame.trim());
	}

	// L E X F I L E

	private static final Map<String, Integer> LEXFILE_TO_NUM = new HashMap<>();

	static
	{
		LEXFILE_TO_NUM.put("adj.all", 0);
		LEXFILE_TO_NUM.put("adj.pert", 1);
		LEXFILE_TO_NUM.put("adv.all", 2);
		LEXFILE_TO_NUM.put("noun.Tops", 3);
		LEXFILE_TO_NUM.put("noun.act", 4);
		LEXFILE_TO_NUM.put("noun.animal", 5);
		LEXFILE_TO_NUM.put("noun.artifact", 6);
		LEXFILE_TO_NUM.put("noun.attribute", 7);
		LEXFILE_TO_NUM.put("noun.body", 8);
		LEXFILE_TO_NUM.put("noun.cognition", 9);
		LEXFILE_TO_NUM.put("noun.communication", 10);
		LEXFILE_TO_NUM.put("noun.event", 11);
		LEXFILE_TO_NUM.put("noun.feeling", 12);
		LEXFILE_TO_NUM.put("noun.food", 13);
		LEXFILE_TO_NUM.put("noun.group", 14);
		LEXFILE_TO_NUM.put("noun.location", 15);
		LEXFILE_TO_NUM.put("noun.motive", 16);
		LEXFILE_TO_NUM.put("noun.object", 17);
		LEXFILE_TO_NUM.put("noun.person", 18);
		LEXFILE_TO_NUM.put("noun.phenomenon", 19);
		LEXFILE_TO_NUM.put("noun.plant", 20);
		LEXFILE_TO_NUM.put("noun.possession", 21);
		LEXFILE_TO_NUM.put("noun.process", 22);
		LEXFILE_TO_NUM.put("noun.quantity", 23);
		LEXFILE_TO_NUM.put("noun.relation", 24);
		LEXFILE_TO_NUM.put("noun.shape", 25);
		LEXFILE_TO_NUM.put("noun.state", 26);
		LEXFILE_TO_NUM.put("noun.substance", 27);
		LEXFILE_TO_NUM.put("noun.time", 28);
		LEXFILE_TO_NUM.put("verb.body", 29);
		LEXFILE_TO_NUM.put("verb.change", 30);
		LEXFILE_TO_NUM.put("verb.cognition", 31);
		LEXFILE_TO_NUM.put("verb.communication", 32);
		LEXFILE_TO_NUM.put("verb.competition", 33);
		LEXFILE_TO_NUM.put("verb.consumption", 34);
		LEXFILE_TO_NUM.put("verb.contact", 35);
		LEXFILE_TO_NUM.put("verb.creation", 36);
		LEXFILE_TO_NUM.put("verb.emotion", 37);
		LEXFILE_TO_NUM.put("verb.motion", 38);
		LEXFILE_TO_NUM.put("verb.perception", 39);
		LEXFILE_TO_NUM.put("verb.possession", 40);
		LEXFILE_TO_NUM.put("verb.social", 41);
		LEXFILE_TO_NUM.put("verb.stative", 42);
		LEXFILE_TO_NUM.put("verb.weather", 43);
		LEXFILE_TO_NUM.put("adj.ppl", 44);
	}

	/**
	 * Code lexfile
	 *
	 * @param name name of lex file
	 * @return code
	 */
	static int codeLexFile(String name)
	{
		return LEXFILE_TO_NUM.get(name);
	}
}
