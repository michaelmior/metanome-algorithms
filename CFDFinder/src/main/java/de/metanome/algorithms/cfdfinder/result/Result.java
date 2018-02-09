package de.metanome.algorithms.cfdfinder.result;

import com.google.common.base.Joiner;
import de.metanome.algorithms.cfdfinder.pattern.*;
import de.metanome.algorithms.cfdfinder.structures.FDTreeElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Result {

    private FDTreeElement.InternalFunctionalDependency embeddedFD;
    private PatternTableau patternTableau;
    private List<String> attributeNames;
    private List<Map<Integer, String>> clusterMaps;

    public static final String NULL_REPRESENTATION = "null";

    public Result(FDTreeElement.InternalFunctionalDependency embeddedFD, PatternTableau patternTableau, List<String> attributeNames, List<Map<Integer, String>> clusterMaps) {
        this.embeddedFD = embeddedFD;
        this.patternTableau = patternTableau;
        this.attributeNames = attributeNames;
        this.clusterMaps = clusterMaps;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        ArrayList<String> bits = new ArrayList<>();
        for (int i = embeddedFD.lhs.nextSetBit(0); i >= 0; i = embeddedFD.lhs.nextSetBit(i + 1)) {
            bits.add(attributeNames.get(i));
        }
        builder.append("[");
        builder.append(StringUtils.join(bits, ','));
        builder.append("] -> ");
        builder.append(attributeNames.get(embeddedFD.rhs));
        builder.append("\nPatternTableau {");
        for (Pattern pattern : patternTableau.getPatterns()) {
            builder.append("\n\t(");
            List<Map.Entry<Integer, PatternEntry>> entries = new ArrayList<>(pattern.getAttributes().entrySet());
            Collections.sort(entries, new Comparator<Map.Entry<Integer, PatternEntry>>() {
                @Override
                public int compare(Map.Entry<Integer, PatternEntry> o1, Map.Entry<Integer, PatternEntry> o2) {
                    return o1.getKey() - o2.getKey();
                }
            });
            List<String> values = new LinkedList<>();
            for (Map.Entry<Integer, PatternEntry> entry : entries) {
                PatternEntry pe = entry.getValue();
                if (pe instanceof VariablePatternEntry) {
                    values.add(pe.toString());
                } else if (pe instanceof RangePatternEntry) {
                    String lowerBound = clusterMaps.get(entry.getKey()).get(((RangePatternEntry) pe).getLowerBound());
                    String upperBound = clusterMaps.get(entry.getKey()).get(((RangePatternEntry) pe).getUpperBound());
                    if (lowerBound == null) {lowerBound = NULL_REPRESENTATION;}
                    if (upperBound == null) {upperBound = NULL_REPRESENTATION;}
                    values.add("[" + lowerBound + " - " + upperBound + "]");
                } else {
                    int value = ((ConstantPatternEntry) pe).getConstant();
                    String s = clusterMaps.get(entry.getKey()).get(value);
                    if (s == null) {
                        s = NULL_REPRESENTATION;
                    }
                    if (pe instanceof NegativeConstantPatternEntry) {
                        s = "¬" + s;
                    }
                    values.add(s);
                }
            }
            builder.append(Joiner.on(",").join(values));
            builder.append(")");
        }
        builder.append("\n}");
        builder.append("\n\tSupport: ");
        builder.append(patternTableau.getSupport());
        builder.append("\n\tConfidence: ");
        builder.append(patternTableau.getConfidence());
        return builder.toString();
    }

    public FDTreeElement.InternalFunctionalDependency getEmbeddedFD() {
        return embeddedFD;
    }

    public void setEmbeddedFD(FDTreeElement.InternalFunctionalDependency embeddedFD) {
        this.embeddedFD = embeddedFD;
    }

    public PatternTableau getPatternTableau() {
        return patternTableau;
    }

    public void setPatternTableau(PatternTableau patternTableau) {
        this.patternTableau = patternTableau;
    }
}
