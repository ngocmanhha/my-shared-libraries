package com.test.jenkins

/**
 * An executable action.
 */
interface Action extends Serializable {
    /**
     * Executes the action.
     *
     * @param script Pipeline script
     * @param configuration configuration map
     */
    void execute(Script script)

    /**
     * @return the action's name, used as stage or parallel branch name
     */
    String name()
}
