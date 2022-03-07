/*******************************************************************************
 * Copyright (c) 2011, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.kernel.feature.internal.subsystem;

public class JavaRange {

    // Inclusive
    int lowerRange = 0;
    int upperRange = Integer.MAX_VALUE;

    public JavaRange(String range) {
        if (range != null) {
            range = range.trim();

            try {
                if (!(range.contains("(") || range.contains("[") || range.contains(")") || range.contains("]"))) {
                    //require-java may accept single numbers, i.e. require-java:=9
                    lowerRange = Integer.parseInt(range);
                    return;
                }

            } catch (NumberFormatException ex) {
                // Do Nothing -- FFDC will automatically report error
            }

            String lowerBound = range.substring(0, range.indexOf(','));
            String upperBound = range.substring(range.indexOf(',') + 1, range.length());

            try {
                switch (lowerBound.charAt(0)) {
                    case '[':
                        lowerRange = Integer.parseInt(lowerBound.substring(1));
                        break;
                    case '(':
                        lowerRange = Integer.parseInt(lowerBound.substring(1)) + 1;
                        break;
                    default:
                        throw new Exception();
                }

                switch (upperBound.charAt(upperBound.length() - 1)) {
                    case ']':
                        upperRange = Integer.parseInt(upperBound.substring(0, upperBound.length() - 1));
                        break;
                    case ')':
                        upperRange = Integer.parseInt(upperBound.substring(0, upperBound.length() - 1)) - 1;
                        break;
                    default:
                        throw new Exception();
                }
            } catch (NumberFormatException ex) {
                // FFDC will automatically report error
            } catch (Exception ex) {
                // FFDC will automatically report error
            }

        }

    }

    public boolean contains(int version) {
        if (version <= upperRange && version >= lowerRange) {
            return true;
        }
        return false;
    }

}