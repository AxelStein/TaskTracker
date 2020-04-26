package com.axel_stein.tasktracker.utils;

import static com.axel_stein.tasktracker.utils.TextUtil.isEmpty;
import static com.axel_stein.tasktracker.utils.TextUtil.notEmpty;

public class ArgsUtil {
    private ArgsUtil() {}

    public static boolean checkRules(CheckRule ...rules) {
        return checkRules(true, rules);
    }

    public static boolean checkRulesSilent(CheckRule ...rules) {
        return checkRules(false, rules);
    }

    public static boolean checkRules(boolean thr, CheckRule ...rules) {
        if (rules != null) {
            for (CheckRule rule : rules) {
                if (!rule.check()) {
                    if (thr) {
                        rule.thr();
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static CheckRule emptyString(final String ...strings) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                if (strings != null) {
                    for (String s : strings) {
                        if (notEmpty(s)) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }

    public static CheckRule notEmptyString(final String ...strings) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                if (strings != null) {
                    for (String s : strings) {
                        if (isEmpty(s)) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }

    public static CheckRule isNull(final Object ...objects) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                if (objects != null) {
                    for (Object o : objects) {
                        if (o != null) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }

    public static CheckRule notNull(final Object ...objects) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                if (objects != null) {
                    for (Object o : objects) {
                        if (o == null) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }

    public static CheckRule inRange(final int val, final int from, final int to) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                return val >= from && val <= to;
            }
        };
    }

    public static CheckRule equalsZero(final int val) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                return val == 0;
            }
        };
    }

    public static CheckRule lessThan(final int val, final int than) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                return val < than;
            }
        };
    }

    public static CheckRule lessThanZero(final int val) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                return val < 0;
            }
        };
    }

    public static CheckRule lessOrEqualsZero(final int val) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                return val <= 0;
            }
        };
    }

    public static CheckRule greaterThanZero(final int val) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                return val > 0;
            }
        };
    }

    public static CheckRule greaterOrEqualsZero(final int val) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                return val >= 0;
            }
        };
    }

    public static CheckRule greaterThan(final int val, final int than) {
        return new ArgumentCheckRule() {
            @Override
            public boolean check() {
                return val > than;
            }
        };
    }

    public interface CheckRule {
        boolean check();
        void thr();
    }

    public static class ArgumentCheckRule implements CheckRule {

        @Override
        public boolean check() {
            return false;
        }

        @Override
        public void thr() {
            throw new IllegalArgumentException();
        }
    }

}
