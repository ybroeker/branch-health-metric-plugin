/*
 * The MIT License
 *
 * Copyright (c) 2018 Yannick Bröker
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.jenkins.plugins.healthmetric.branch;

import java.util.Collections;
import java.util.List;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.hudson.plugins.folder.health.*;
import hudson.Extension;
import hudson.model.HealthReport;
import hudson.model.Item;
import org.kohsuke.stapler.DataBoundConstructor;

import static io.jenkins.plugins.healthmetric.branch.Messages.*;

public class SpecificBranch extends FolderHealthMetric {

    private static final String DEFAULT_BRANCH ="master";

    private final String branch;

    @DataBoundConstructor
    public SpecificBranch(String branch) {
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }

    @Override
    public Type getType() {
        return Type.IMMEDIATE_TOP_LEVEL_ITEMS;
    }

    @Override
    public Reporter reporter() {
        return new SpecificBranch.ReporterImpl(branch);
    }

    @Extension(ordinal = 400)
    public static class DescriptorImpl extends FolderHealthMetricDescriptor {

        @Override
        public String getDisplayName() {
            return BranchHealthMetric_DisplayName();
        }

        @Override
        public FolderHealthMetric createDefault() {
            return new SpecificBranch(DEFAULT_BRANCH);
        }

    }

    private static class ReporterImpl implements Reporter {
        HealthReport branchReport = null;

        private final String branch;

        private ReporterImpl(final String branch) {
            this.branch = branch;
        }

        public void observe(Item item) {
            if (item instanceof Folder) {
                // only interested in non-folders in order to prevent double counting
                return;
            }
            if (item.getName().equals(branch)) {
                branchReport = getHealthReport(item);
            }
        }

        public List<HealthReport> report() {
            return branchReport != null
                   ? Collections.singletonList(branchReport)
                   : Collections.<HealthReport>emptyList();
        }
    }
}
