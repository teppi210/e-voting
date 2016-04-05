package uk.dsxt.voting.common.demo;

import uk.dsxt.voting.common.domain.dataModel.VoteResult;
import uk.dsxt.voting.common.domain.dataModel.VoteStatus;
import uk.dsxt.voting.common.domain.dataModel.Voting;
import uk.dsxt.voting.common.domain.nodes.NetworkClient;
import uk.dsxt.voting.common.domain.nodes.NetworkMessagesSender;
import uk.dsxt.voting.common.utils.InternalLogicException;

public class ResultBilderDecorator implements NetworkClient {

    private final NetworkClient networkClient;

    private final ResultsBuilder resultsBuilder;

    private final String holderId;

    public ResultBilderDecorator(ResultsBuilder resultsBuilder, NetworkClient networkClient, String holderId) {
        this.resultsBuilder = resultsBuilder;
        this.networkClient = networkClient;
        this.holderId = holderId;
    }

    @Override
    public void addVoting(Voting voting) {
        networkClient.addVoting(voting);
    }

    @Override
    public void addVotingTotalResult(VoteResult result) {
        networkClient.addVotingTotalResult(result);
        resultsBuilder.addResult(holderId, result.toString());
    }

    @Override
    public void addVoteStatus(VoteStatus status) {
        networkClient.addVoteStatus(status);
    }

    @Override
    public void addVote(VoteResult result, String messageId, String serializedResult) {
        networkClient.addVote(result, messageId, serializedResult);
    }

    @Override
    public void setNetworkMessagesSender(NetworkMessagesSender networkMessagesSender) {
        networkClient.setNetworkMessagesSender(new NetworkMessagesSender() {
            @Override
            public String addVoting(Voting voting) throws InternalLogicException {
                return networkMessagesSender.addVoting(voting);
            }

            @Override
            public String addVotingTotalResult(VoteResult result, Voting voting) throws InternalLogicException {
                return networkMessagesSender.addVotingTotalResult(result, voting);
            }

            @Override
            public String addVoteStatus(VoteStatus status) throws InternalLogicException {
                return networkMessagesSender.addVoteStatus(status);
            }

            @Override
            public String addVote(VoteResult result, String serializedVote, String ownerSignature, String nodeSignature) throws InternalLogicException {
                resultsBuilder.addVote(result.toString());
                return networkMessagesSender.addVote(result, serializedVote, ownerSignature, nodeSignature);
            }
        });
    }
}
