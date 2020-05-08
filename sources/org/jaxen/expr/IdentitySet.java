package org.jaxen.expr;

import java.util.HashSet;

final class IdentitySet {
    private HashSet contents = new HashSet();

    private static class IdentityWrapper {
        private Object object;

        IdentityWrapper(Object object2) {
            this.object = object2;
        }

        public boolean equals(Object o) {
            return this.object == ((IdentityWrapper) o).object;
        }

        public int hashCode() {
            return System.identityHashCode(this.object);
        }
    }

    IdentitySet() {
    }

    /* access modifiers changed from: 0000 */
    public void add(Object object) {
        this.contents.add(new IdentityWrapper(object));
    }

    public boolean contains(Object object) {
        return this.contents.contains(new IdentityWrapper(object));
    }
}
