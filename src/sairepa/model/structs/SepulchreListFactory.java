package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

import net.kwain.fxie.XBaseFieldType;

public class SepulchreListFactory extends ActListFactory
{
	public static FieldLayout fields = null;

	private static boolean isEmpty(ActEntry e) {
		return ("".equals(e.getValue().trim()) || "-".equals(e.getValue().trim()));
	}

	private static class AdultTest implements Test {
		private ActField lastName2;
		private ActField firstName2;
		private ActField lastName3;
		private ActField firstName3;
		private ActField lastNameC;
		private ActField firstNameC;

		public AdultTest(ActField lastName2, ActField lastName3, ActField lastNameC,
				ActField firstName2, ActField firstName3, ActField firstNameC) {
			this.lastName2 = lastName2;
			this.lastName3 = lastName3;
			this.lastNameC = lastNameC;
			this.firstName2 = firstName2;
			this.firstName3 = firstName3;
			this.firstNameC = firstNameC;

			Util.check(lastName2 != null);
			Util.check(lastName3 != null);
			Util.check(lastNameC != null);
			Util.check(firstName2 != null);
			Util.check(firstName3 != null);
			Util.check(firstNameC != null);
		}

		public boolean test(Act a) {
			boolean name2e = isEmpty(a.getEntry(lastName2)) && isEmpty(a.getEntry(firstName2));
			boolean name3e = isEmpty(a.getEntry(lastName3)) && isEmpty(a.getEntry(firstName3));
			boolean nameCe = isEmpty(a.getEntry(lastNameC)) && isEmpty(a.getEntry(firstNameC));

			boolean t = ( !nameCe || (name2e && name3e) );
			return t;
		}
	}

	private static class NonEmptyTest implements Test {
		private ActField field1;
		private ActField field2;

		public NonEmptyTest(ActField field1, ActField field2) {
			this.field1 = field1;
			this.field2 = field2;
		}

		public boolean test(Act a) {
			return !(isEmpty(a.getEntry(field1)) && isEmpty(a.getEntry(field2)));
		}
	}

	static {
		ActField tmpLastName1 = null;
		ActField tmpFirstName1 = null;
		ActField tmpLastName2 = null;
		ActField tmpFirstName2 = null;
		ActField tmpLastName3 = null;
		ActField tmpFirstName3 = null;
		ActField tmpLastNameC = null;
		ActField tmpFirstNameC = null;
		SexField tmpSexField = null;

		try {
			fields = new FieldLayout(new FieldLayoutElement[] {
				new NumericField("JOUR", 2, 0, 31),
				   new NumericField("MOIS", 2, 0, 12),
				   new NumericField("ANNEE", 4, 1500, 2020),
				   new FieldLayout("Renseignements concernant le d\351funt",
					   new FieldLayoutElement[] {
						   tmpFirstName1 = new FirstNameField("PRN1"),
				   tmpLastName1 = new LastNameField("NOM1", Sex.UNKNOWN),
				   tmpSexField = new SexField("SEX1"),
				   new ActField("AGE1", 13, new XBaseFieldType.XBaseFieldTypeString()),
				   new ActField("NOT1", 40, new XBaseFieldType.XBaseFieldTypeString()),
					   }),
				   new FieldLayout("P\350re du d\351funt",
					   new FieldLayoutElement[] {
						   tmpFirstName2 = new FirstNameField("PRN2"),
				   tmpLastName2 = new LastNameField("NOM2", Sex.MALE, tmpLastName1),
				   new ActField("NOT2", 40, new XBaseFieldType.XBaseFieldTypeString()),
					   }),
				   new FieldLayout("M\350re du d\351funt",
					   new FieldLayoutElement[] {
						   tmpFirstName3 = new FirstNameField("PRN3"),
				   tmpLastName3 = new LastNameField("NOM3", Sex.FEMALE),
				   new ActField("NOT3", 40, new XBaseFieldType.XBaseFieldTypeString()),
					   }),
				   new FieldLayout("Conjoint du d\351funt",
						   new FieldLayoutElement[] {
							   tmpFirstNameC = new FirstNameField("PRNC"),
				   tmpLastNameC = new LastNameField("NOMC", Sex.UNKNOWN),
				   new ActField("NOTC", 40, new XBaseFieldType.XBaseFieldTypeString()),
						   }),
				   // the following part gives me a freaking headache.
				   new FieldLayout("Noms/Pr\351noms conventionnels",
						   new FieldLayoutElement[] {
							   new ConditionalField("PRN2CV", 20, new XBaseFieldType.XBaseFieldTypeString(),
								   new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC,
									   tmpFirstName2, tmpFirstName3, tmpFirstNameC),
								   new ConvNameField("PRN2CV", ConvNameField.PRN_DEFAULT_LNG, Conventionalizer.CONV_FIRST_NAME,
									   tmpSexField, tmpFirstName1, tmpFirstNameC, null, Sex.MALE),
								   new ConvNameField("PRN2CV", ConvNameField.PRN_DEFAULT_LNG, Conventionalizer.CONV_FIRST_NAME,
									   Sex.MALE, tmpFirstName2)),
							   new ConditionalField("NOM2CV", 20, new XBaseFieldType.XBaseFieldTypeString(),
								   new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC,
									   tmpFirstName2, tmpFirstName3, tmpFirstNameC),
								   new ConvNameField("NOM2CV", ConvNameField.NOM_DEFAULT_LNG, Conventionalizer.CONV_LAST_NAME,
									   tmpSexField, tmpLastName1, tmpLastNameC, null, Sex.MALE),
								   new ConditionalField("NOM2CV", ConvNameField.NOM_DEFAULT_LNG,
									   new XBaseFieldType.XBaseFieldTypeString(),
									   new NonEmptyTest(tmpLastName2, tmpFirstName2),
									   new ConvNameField("NOM2CV", ConvNameField.NOM_DEFAULT_LNG, Conventionalizer.CONV_LAST_NAME,
										   Sex.MALE, tmpLastName2),
									   new ConvNameField("NOM2CV", ConvNameField.NOM_DEFAULT_LNG, Conventionalizer.CONV_LAST_NAME,
										   Sex.MALE, tmpLastName1))),
							   new ConditionalField("PRN3CV", 20, new XBaseFieldType.XBaseFieldTypeString(),
									   new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC,
										   tmpFirstName2, tmpFirstName3, tmpFirstNameC),
									   new ConvNameField("PRN3CV", ConvNameField.PRN_DEFAULT_LNG, Conventionalizer.CONV_FIRST_NAME,
										   tmpSexField, tmpFirstNameC, tmpFirstName1, null, Sex.FEMALE),
									   new ConvNameField("PRN3CV", ConvNameField.PRN_DEFAULT_LNG, Conventionalizer.CONV_FIRST_NAME,
										   Sex.FEMALE, tmpFirstName3, "?")),
							   new ConditionalField("NOM3CV", 20, new XBaseFieldType.XBaseFieldTypeString(),
									   new AdultTest(tmpLastName2, tmpLastName3, tmpLastNameC,
										   tmpFirstName2, tmpFirstName3, tmpFirstNameC),
									   new ConvNameField("NOM3CV", ConvNameField.NOM_DEFAULT_LNG, Conventionalizer.CONV_LAST_NAME,
										   tmpSexField, tmpLastNameC, tmpLastName1, null, Sex.FEMALE),
									   new ConvNameField("NOM3CV", ConvNameField.NOM_DEFAULT_LNG, Conventionalizer.CONV_LAST_NAME,
										   Sex.FEMALE, tmpLastName3, "?")),
						   }),
						   new FieldLayout("Renseignement divers concernant l'acte",
								   new FieldLayoutElement[] {
									   new ActField("DIVERS", 40, new XBaseFieldType.XBaseFieldTypeString()),
						   new LocalityField("LOC1ID"),
						   new ActField("TXTLONG", 5000, new XBaseFieldType.XBaseFieldTypeMemo()),
								   }),
			});
		} catch (IOException e) {
			System.err.println("Can't instanciate field prototypes because : " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}
	}

	public SepulchreListFactory(Model m, File projectDir)
		throws java.io.FileNotFoundException {
		super(m, Util.getFile(projectDir, "sepultu5.dbf"), Util.getFile(projectDir, "sepultu5.dbt"), fields);
	}

	public String toString() {
		return "S\351pultures";
	}
}
