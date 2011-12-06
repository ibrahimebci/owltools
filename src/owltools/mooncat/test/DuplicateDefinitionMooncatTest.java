package owltools.mooncat.test;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

import org.junit.Test;
import org.obolibrary.obo2owl.Owl2Obo;
import org.obolibrary.oboformat.model.Clause;
import org.obolibrary.oboformat.model.Frame;
import org.obolibrary.oboformat.model.OBODoc;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.obolibrary.oboformat.writer.OBOFormatWriter;
import org.semanticweb.owlapi.model.OWLOntology;

import owltools.graph.OWLGraphWrapper;
import owltools.io.ParserWrapper;
import owltools.mooncat.Mooncat;
import owltools.mooncat.OntologyMetaDataTools;
import owltools.test.OWLToolsTestBasics;

/**
 * Test for {@link Mooncat} and {@link OntologyMetaDataTools}. This test 
 * case simulates conflicting values for a single term definition from 
 * different ontology files.
 */
public class DuplicateDefinitionMooncatTest extends OWLToolsTestBasics {

	private static boolean REDER_ONTOLOGY_FLAG = true;
	
	@Test
	public void testMireot() throws Exception {
		ParserWrapper pw = new ParserWrapper();
		
		OWLGraphWrapper g = pw.parseToOWLGraph(getResourceIRIString("mooncat/A.obo"));
		Mooncat m = new Mooncat(g);
		m.addReferencedOntology(pw.parseOBO(getResourceIRIString("mooncat/B.obo")));
		m.addReferencedOntology(pw.parseOBO(getResourceIRIString("mooncat/C.obo")));
		
		m.mergeOntologies();
		
		OWLOntology sourceOntology = g.getSourceOntology();
		OntologyMetaDataTools.checkAnnotationCardinality(sourceOntology);
		
		Owl2Obo owl2Obo = new Owl2Obo();
		OBODoc oboDoc = owl2Obo.convert(sourceOntology);
		if (REDER_ONTOLOGY_FLAG) {
			renderOBO(oboDoc);
		}
		Frame termFrame = oboDoc.getTermFrame("C:0000001");
		Collection<Clause> clauses = termFrame.getClauses(OboFormatTag.TAG_DEF);
		assertEquals(1, clauses.size());
	}
	
	private void renderOBO(OBODoc oboDoc) throws IOException {
		OBOFormatWriter writer = new OBOFormatWriter();
		writer.setCheckStructure(true);
		StringWriter out = new StringWriter();
		BufferedWriter stream = new BufferedWriter(out);
		writer.write(oboDoc, stream);
		stream.close();
		System.out.println(out.getBuffer());
	}
	
}