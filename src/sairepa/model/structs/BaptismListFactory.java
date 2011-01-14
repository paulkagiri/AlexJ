package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

import net.kwain.fxie.XBaseFieldType;

public class BaptismListFactory extends ActListFactory
{
	public static FieldLayout fields = null;

	static {
		ActField tmpLastName1;
		ActField tmpLastName2;
		ActField tmpFirstName2;
		ActField tmpLastName3;
		ActField tmpFirstName3;

		try {
			fields = new FieldLayout(new FieldLayoutElement[] {
				new NumericField("JOUR", 2, 0, 31),
				   new NumericField("MOIS", 2, 0, 12),
				   new NumericField("ANNEE", 4, 1500, 2020),
				   new FieldLayout("Renseignements concernant le nouveau n\351",
					   new FieldLayoutElement[] {
						   new FirstNameField("PRN1"),
				   tmpLastName1 = new LastNameField("NOM1", Sex.UNKNOWN),
				   new SexField("SEX1"),
				   new ActField("NEE", 7, new XBaseFieldType.XBaseFieldTypeString()),
				   new ActField("NOT1", 40, new XBaseFieldType.XBaseFieldTypeString()),
					   }),
				   new FieldLayout("Informations concernant le p\350re du nouveau n\351",
					   new FieldLayoutElement[] {
						   tmpFirstName2 = new FirstNameField("PRN2"),
				   tmpLastName2 = new LastNameField("NOM2", Sex.MALE, tmpLastName1),
				   new ActField("NOT2", 40, new XBaseFieldType.XBaseFieldTypeString()),
					   }),
				   new FieldLayout("Informations concernant la m\350re du nouveau n\351",
					   new FieldLayoutElement[] {
						   tmpFirstName3 = new FirstNameField("PRN3"),
				   tmpLastName3 = new LastNameField("NOM3", Sex.FEMALE),
				   new ActField("NOT3", 40, new XBaseFieldType.XBaseFieldTypeString()),
					   }),
				   new FieldLayout("Informations concernant le parrain",
						   new FieldLayoutElement[] {
							   new FirstNameField("PRNP"),
				   new LastNameField("NOMP", Sex.MALE),
				   new ActField("NOTP", 40, new XBaseFieldType.XBaseFieldTypeString()),
						   }),
				   new FieldLayout("Informations concernant la marraine",
						   new FieldLayoutElement[] {
							   new FirstNameField("PRNM"),
				   new LastNameField("NOMM", Sex.FEMALE),
				   new ActField("NOTM", 40, new XBaseFieldType.XBaseFieldTypeString()),
						   }),
				   new FieldLayout("Noms/Pr\351noms conventionnels",
						   new FieldLayoutElement[] {
							   new ConvNameField("PRN2CV", ConvNameField.PRN_DEFAULT_LNG, Conventionalizer.CONV_FIRST_NAME,
								   Sex.MALE, tmpFirstName2),
							   new ConvNameField("NOM2CV", ConvNameField.NOM_DEFAULT_LNG, Conventionalizer.CONV_LAST_NAME,
								   Sex.MALE, tmpLastName2),
							   new ConvNameField("PRN3CV", ConvNameField.PRN_DEFAULT_LNG, Conventionalizer.CONV_FIRST_NAME,
								   Sex.FEMALE, tmpFirstName3),
							   new ConvNameField("NOM3CV", ConvNameField.NOM_DEFAULT_LNG, Conventionalizer.CONV_LAST_NAME,
								   Sex.FEMALE, tmpLastName3),
						   }),
				   new FieldLayout("Renseignements divers concernant l'acte",
						   new FieldLayoutElement[] {
							   new ActField("DIVERS", 40, new XBaseFieldType.XBaseFieldTypeString()),
				   new LocalityField("LOC2ID"),
				   new ActField("TXTLONG", 5000, new XBaseFieldType.XBaseFieldTypeMemo()),
						   }),
			});
		} catch (IOException e) {
			System.err.println("Can't instanciate field prototypes because : " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}
	}

	public BaptismListFactory(Model m, File projectDir)
		throws java.io.FileNotFoundException {
		this(m, projectDir, "bapteme5.dbf", "bapteme5.dbt");
	}

	protected BaptismListFactory(Model m, File projectDir, String dbf, String dbt)
		throws java.io.FileNotFoundException {
		super(m, Util.getFile(projectDir, dbf), Util.getFile(projectDir, dbt), fields);
	}

	public String toString() {
		return "Bapt\352mes";
	}
}
