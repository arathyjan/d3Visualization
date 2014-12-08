package com.barclays.ivr.viz.domain.elements;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.jdom2.Element;

import java.util.Collection;

import static com.google.common.collect.Lists.transform;
import static java.util.Arrays.asList;

public interface IvrElement {

    Function<Element, IvrElement> TO_IVR_ELEMENT = new Function<Element, IvrElement>() {
        @Override
        public IvrElement apply(Element input) {
            return Type.parse(input.getName()).create(input);
        }
    };
    Predicate<Element> BY_TYPE = new Predicate<Element>() {
        @Override
        public boolean apply(Element input) {
            return Type.ALL.contains(input.getName());
        }
    };

    void accept(IvrElementVisitor visitor);

    public static enum Type {
        IF("IF") {
            @Override
            public IvrElement create(Element element) {
                return new IfElement(element);
            }
        }, CALL("call") {
            @Override
            public IvrElement create(Element element) {
                return new CallElement(element);
            }
        }, ELIF("ELIF") {
            @Override
            public IvrElement create(Element element) {
                return new ElifElement(element);
            }
        }, ELSE("ELSE") {
            @Override
            public IvrElement create(Element element) {
                return new ElseElement();
            }
        }, STARTSTATEOVER("startStateOver") {
            @Override
            public IvrElement create(Element element) {
                return new StartOverElement();
            }
        }, GOTO("goto") {
            @Override
            public IvrElement create(Element element) {
                return new GoToElement(element);
            }
        };
        public static final Function<Type, String> TO_TYPE_STRING = new Function<Type, String>() {
            @Override
            public String apply(Type input) {
                return input.type;
            }
        };
        public static final Collection<String> ALL = transform(asList(values()), TO_TYPE_STRING);

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public static Type parse(String type) {
            return Type.valueOf(type.toUpperCase());
        }

        public abstract IvrElement create(Element element);
    }
}
