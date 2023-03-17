package principal.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import principal.modelo.Ejercicio;
import principal.modelo.Rutina;
import principal.modelo.Alumno;
import principal.modelo.Entrenador;
import principal.servicio.impl.EjercicioServiceImpl;
import principal.servicio.impl.RutinaServiceImpl;
import principal.servicio.impl.EntrenadorServiceImpl;
import principal.servicio.impl.AlumnoServiceImpl;

@RequestMapping("/rutinas")
@Controller
public class RutinaController {

	@Autowired
	private EjercicioServiceImpl ejercicioService;
	
	@Autowired
	private RutinaServiceImpl rutinaService;
	
	@Autowired
	private EntrenadorServiceImpl entrenadorService;
	
	@Autowired
	private AlumnoServiceImpl alumnoService;
		
		@GetMapping(value= {"","/"})
		String homerutinas(Model model) {
			
			// Salir a buscar a la BBDD
			
			ArrayList<Ejercicio> misejercicios = (ArrayList<Ejercicio>) ejercicioService.listarEjercicios();
			ArrayList<Rutina> misrutinas = (ArrayList<Rutina>) rutinaService.listarRutinas();
			ArrayList<Entrenador> misentrenadores = (ArrayList<Entrenador>) entrenadorService.listarEntrenadors();
			ArrayList<Alumno> misalumnos = (ArrayList<Alumno>) alumnoService.listarAlumnos();
			
			model.addAttribute("listaEjercicios", misejercicios);
			model.addAttribute("listaRutinas",misrutinas);
			model.addAttribute("listaEntrenadores", misentrenadores);
			model.addAttribute("listaalumnos",misalumnos);
			model.addAttribute("rutinaaEditar", new Rutina());
			model.addAttribute("rutinaNuevo", new Rutina());
			
			
			return "rutinas";
		}
		
		@PostMapping("/edit/{id}")
		public String editarRutina(@PathVariable Integer id, @ModelAttribute("rutinaaEditar") Rutina rutinaEditado, BindingResult bindingresult) {
			
			Rutina rutinaaEditar = rutinaService.obtenerRutinaPorID(id);
			
			rutinaaEditar.setNombre(rutinaEditado.getNombre());
			

			for(Ejercicio ej:rutinaaEditar.getEjercicios()) {
				if(!rutinaEditado.getEjercicios().contains(ej)) {
					ej.getRutinas().remove(rutinaaEditar);
				}
			}
			
			for(Ejercicio ej:rutinaEditado.getEjercicios()) {
				if(!rutinaaEditar.getEjercicios().contains(ej)) {
					ej.getRutinas().add(rutinaEditado);
				}
			}
			
			rutinaService.insertarRutina(rutinaEditado);
			
			return "redirect:/rutinas";
		}
		
		@PostMapping("/add")
		public String addRutina(@ModelAttribute("rutinaNuevo") Rutina rutinaNew, BindingResult bindingresult,Integer id) {
			
			for(Ejercicio e: rutinaNew.getEjercicios()) {
				Ejercicio e2 = e;
				e2.getRutinas().add(rutinaNew);
			}
			
			rutinaService.insertarRutina(rutinaNew);
			
			return "redirect:/rutinas";
		}
		
		@GetMapping({"/{id}"})
		String idUsuario(Model model, @PathVariable Integer id) {
			
			Rutina rutinaMostrar = rutinaService.obtenerRutinaPorID(id);
			model.addAttribute("rutinaMostrar",rutinaMostrar);
			
			
			return "rutina";
		}
		
		@GetMapping("/delete/{id}")
		String deleteRutina(Model model, @PathVariable Integer id) {
			   Ejercicio ejercicio = ejercicioService.obtenerEjercicioPorID(id);
			   Alumno alumno = alumnoService.obtenerAlumnoPorID(id);
			   Entrenador entrenador = entrenadorService.obtenerEntrenadorPorID(id);
			   Rutina rutina = rutinaService.obtenerRutinaPorID(id);
			   
			   for(Alumno a: rutina.getAlumnos()){
				   a.getRutinas().remove(rutina);
				   rutina.getAlumnos().remove(alumno);
			   }
			   
			   for(Entrenador e: rutina.getEntrenadores()){
				   e.getRutinas().remove(rutina);
				   rutina.getEntrenadores().remove(entrenador);
			   }
			   
			   for(Ejercicio ej: rutina.getEjercicios()){
				   ej.getRutinas().remove(rutina);
				   rutina.getEjercicios().remove(ejercicio);
			   }
			   
			rutinaService.eliminarRutinaPorId(id);

			
			return "redirect:/rutinas";
		}

}
