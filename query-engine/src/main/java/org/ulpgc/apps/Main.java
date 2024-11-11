package org.ulpgc.apps;
import org.ulpgc.control.QueryEngineController;

import org.ulpgc.exceptions.QueryEngineException;

public class Main {
    public static void main(String[] args) throws QueryEngineException{
        // Llamar al método QueryEngineController desde la clase en el paquete org.ulpgc.control
        QueryEngineController.Controller();
    }
}
