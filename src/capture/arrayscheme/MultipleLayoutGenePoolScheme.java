package capture.arrayscheme;

import java.util.ArrayList;
import java.util.Collection;

import nextgen.core.pipeline.ConfigFile;
import nextgen.core.pipeline.ConfigFileOptionValue;

import capture.OligoPool;
import capture.ProbeSet;

import broad.core.sequence.Sequence;

public class MultipleLayoutGenePoolScheme implements PoolScheme {
	
	private Collection<ProbeLayout> probeLayouts;

	
	public MultipleLayoutGenePoolScheme() {}
	
	public MultipleLayoutGenePoolScheme(Collection<ProbeLayout> layouts) {
		probeLayouts = layouts;
	}
	
	
	@Override
	public String name() {
		return "multiple_layout_gene_pool_scheme";
	}

	@Override
	public String configFileLineDescription() {
		return OligoPool.poolSchemeOptionFlag + "\t" + name();
	}

	@Override
	public boolean validConfigFileValue(ConfigFileOptionValue value) {
		return value.getActualNumValues() == 2 && value.asString(0).equals(OligoPool.poolSchemeOptionFlag) && value.asString(1).equals(name());
	}

	@Override
	public void setParametersFromConfigFile(ConfigFileOptionValue value) {
		throw new UnsupportedOperationException("Method not applicable.");
	}

	@Override
	public Collection<ProbeSet> getProbes(Collection<Sequence> transcripts) {
		Collection<ProbeSet> rtrn = new ArrayList<ProbeSet>();
		for(Sequence transcript : transcripts) {
			if (transcript.getSequenceBases().toUpperCase().indexOf("GNIL") != -1) {
				throw new IllegalArgumentException("Found GNIL");
			}
			for(ProbeLayout layout : probeLayouts) {
				ProbeSet probesThisSequence = layout.getProbes(transcript);
				probesThisSequence.setName(layout.toString() + "_" + transcript.getId());
				rtrn.add(probesThisSequence);
			}
		}
		return rtrn;
	}

	@Override
	public void setFromConfigFile(ConfigFile file) {
		ConfigFileOptionValue value = file.getSingleValue(OligoPool.arraySchemeSection, OligoPool.poolSchemeOption);
		if(!validConfigFileValue(value)) {
			throw new IllegalArgumentException("File line not valid:\n" + value.getFullOptionLine() + "\nlineformat:\n" + configFileLineDescription());
		}
		Collection<ProbeLayout> layouts = OligoPool.getProbeLayoutsFromConfigFile(file);
		if(layouts.size() < 1) {
			throw new IllegalArgumentException("To use pool scheme " + name() + " must provide at least one probe layout in config file.");
		}
		probeLayouts = layouts;
	}

}
