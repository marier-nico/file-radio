package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestOctetBinaire.class,
	TestPasserelleFichier.class,
	TestRepresentationBinaire.class,
	TestGenerateurSon.class,
	TestLecteurSon.class,
	TestReconstitueurDeMessages.class})

public class TestSuite {

}
