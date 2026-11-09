package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccsw
 *
 */
@Service
@Transactional
public class LoanServiceImpl implements com.ccsw.tutorial.loan.LoanService {

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    GameService gameService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Loan> findPage(LoanSearchDto dto) {

        LoanSpecification clientSpec = new LoanSpecification(new SearchCriteria("client.id", ":", dto.getIdClient()));
        LoanSpecification gameSpec = new LoanSpecification(new SearchCriteria("game.id", ":", dto.getIdGame()));
        LoanSpecification dateSpec = new LoanSpecification(new SearchCriteria("date", "date", dto.getDate()));

        Specification<Loan> spec = Specification.where(clientSpec).and(gameSpec).and(dateSpec);
        // Desde la versión 3.5.0 de Spring Boot, la nueva manera es

        return this.loanRepository.findAll(spec, dto.getPageable().getPageable());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, LoanDto dto) {

        if (dto.getReturnDate().before(dto.getLoanDate())) {
            throw new RuntimeException("La fecha de devolución no puede ser anterior a la fecha de préatamo");
        }

        long difference = dto.getReturnDate().getTime() - dto.getLoanDate().getTime();

        long days = difference / (1000 * 60 * 60 * 24);

        if (days > 14) {
            throw new RuntimeException("La duración máxima de un préstamo es de 14 días");
        }

        Loan loan;

        if (id == null) {
            loan = new Loan();
        } else {
            loan = this.loanRepository.findById(id).orElse(null);
        }

        BeanUtils.copyProperties(dto, loan, "id", "game", "client");

        loan.setClient(clientService.get(dto.getClient().getId()));
        loan.setGame(gameService.get(dto.getGame().getId()));

        List<Loan> gameLoans = this.loanRepository.findByGameId(dto.getGame().getId());

        for (Loan existingLoan : gameLoans) {
            if (id != null && existingLoan.getId().equals(id)) {
                continue;
            }

            boolean solapan = dto.getLoanDate().compareTo(existingLoan.getReturnDate()) <= 0 && dto.getReturnDate().compareTo(existingLoan.getLoanDate()) >= 0;

            if (solapan) {
                throw new RuntimeException("El juego ya está prestado en las fechas seleccionadas");
            }
        }

        List<Loan> clientLoans = this.loanRepository.findByClientId(dto.getClient().getId());

        int numLoans = 0;

        for (Loan existingLoan : clientLoans) {
            if (id != null && existingLoan.getId().equals(id)) {
                continue;
            }

            boolean solapanLoans = dto.getLoanDate().compareTo(existingLoan.getReturnDate()) <= 0 && dto.getReturnDate().compareTo(existingLoan.getLoanDate()) >= 0;

            if (solapanLoans) {
                numLoans++;
            }
        }

        if (numLoans >= 2) {
            throw new RuntimeException("El cliente no puede tener más de 2 préstamos simultáneos");
        }

        this.loanRepository.save(loan);
    }

    @Override
    public Loan get(Long id) {
        return this.loanRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {

        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }

        this.loanRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Loan> findAll() {

        return (List<Loan>) this.loanRepository.findAll();
    }

}
