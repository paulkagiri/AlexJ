package sairepa.model.structs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import sairepa.model.*;
import sairepa.model.fields.*;

import net.kwain.fxie.XBaseFieldType;

public class WeddingListFactory extends ActListFactory
{
	public static FieldLayout fields = null;

	static {
		ActField tmpLastName2;
		ActField tmpFirstName2;
		ActField tmpLastName3;
		ActField tmpFirstName3;

		try {
			fields = new FieldLayout(new FieldLayoutElement[] {
				new NumericField("JOUR", 2, 0, 31),
				   new NumericField("MOIS", 2, 0, 31),
				   new NumericField("ANNEE", 4, 1500, 2020),
				   new FieldLayout("Renseignement concernant l'\351poux",
					   new FieldLayoutElement[] {
						   tmpFirstName2 = new FirstNameField("PRN2"),
				   tmpLastName2 = new LastNameField("NOM2", Sex.MALE),
				   new ActField("LOC2", 50, new XBaseFieldType.XBaseFieldTypeString()),
				   new LocalityField("LOC2ID"),
				   new CVField("CVF2"),
				   new ActField("NOT2", 40, new XBaseFieldType.XBaseFieldTypeString()),
				   new FieldLayout("P\350re de l'\351poux",
					   new FieldLayoutElement[] {
						   new FirstNameField("PRN4"),
				   new LastNameField("NOM4", Sex.MALE, tmpLastName2),
				   new ActField("NOT4", 40, new XBaseFieldType.XBaseFieldTypeString())
					   }),
				   new FieldLayout("M\350re de l'\351poux",
					   new FieldLayoutElement[] {
						   new FirstNameField("PRN5"),
				   new LastNameField("NOM5", Sex.FEMALE),
					   })
					   }),
				   new FieldLayout("Renseignement concernant l'\351pouse",
						   new FieldLayoutElement[] {
							   tmpFirstName3 = new FirstNameField("PRN3"),
				   tmpLastName3 = new LastNameField("NOM3", Sex.FEMALE),
				   new ActField("LOC3", 50, new XBaseFieldType.XBaseFieldTypeString()),
				   new LocalityField("LOC3ID"),
				   new CVField("CVF3"),
				   new ActField("NOT3", 40, new XBaseFieldType.XBaseFieldTypeString()),
				   new FieldLayout("P\350re de l'\351pouse",
					   new FieldLayoutElement[] {
						   new FirstNameField("PRN6"),
				   new LastNameField("NOM6", Sex.MALE, tmpLastName3),
				   new ActField("NOT6", 40, new XBaseFieldType.XBaseFieldTypeString()),
					   }),
				   new FieldLayout("M\350re de l'\351pouse",
					   new FieldLayoutElement[] {
						   new FirstNameField("PRN7"),
				   new LastNameField("NOM7", Sex.FEMALE),
					   }),
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
				   new FieldLayout("Renseignement divers concernant l'acte",
						   new FieldLayoutElement[] {
							   new ActField("TEMOIN", 50, new XBaseFieldType.XBaseFieldTypeString()),
				   new ActField("DIVERS", 40, new XBaseFieldType.XBaseFieldTypeString()),
				   new ActField("TXTLONG", 5000, new XBaseFieldType.XBaseFieldTypeMemo()),
						   })
			});
		} catch (IOException e) {
			System.err.println("Can't instanciate field prototypes because : " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}
	}

	public WeddingListFactory(Model m, File projectDir)
		throws java.io.FileNotFoundException {
		super(m, Util.getFile(projectDir, "mariage5.dbf"), Util.getFile(projectDir, "mariage5.dbt"), fields);
	}

	public String toString() {
		return "Mariages";
	}
}
